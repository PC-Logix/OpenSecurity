-- OpenSecurity entity detector + energy turret proof of concept.
-- Place both components on the same OC network and provide plenty of power.

local component = require("component")
local computer = require("computer")

local TARGET_NAME = "zombie"
local SCAN_RANGE = 16
local SCAN_DELAY = 0.25

-- Detector block position minus turret block position.
-- Example: detector is one block west of the turret: {x = -1, y = 0, z = 0}
local DETECTOR_OFFSET = {x = -1, y = 0, z = 0}

-- Projectile origin inside the turret block. Use 0.9 for a floor-mounted
-- turret and 0.1 for a ceiling-mounted turret.
local MUZZLE_Y = 0.9

local function firstComponent(componentType)
  local address = component.list(componentType)()
  assert(address, "missing component: " .. componentType)
  return component.proxy(address)
end

local detector = firstComponent("os_entdetector")
local turret = firstComponent("os_energyturret")
local atan2 = math.atan2 or function(y, x) return math.atan(y, x) end

local function nearestTarget(entities)
  local best
  for _, entity in pairs(entities) do
    if type(entity) == "table"
        and type(entity.name) == "string"
        and entity.name:lower() == TARGET_NAME
        and (not best or entity.range < best.range) then
      best = entity
    end
  end
  return best
end

local function aimAt(entity)
  -- Entity coordinates are relative to the detector's block corner. Convert
  -- them to a vector from the turret's projectile origin to the entity center.
  local dx = DETECTOR_OFFSET.x + entity.x - 0.5
  local dy = DETECTOR_OFFSET.y + entity.y + entity.height * 0.55 - MUZZLE_Y
  local dz = DETECTOR_OFFSET.z + entity.z - 0.5
  local horizontal = math.sqrt(dx * dx + dz * dz)

  -- Matches OpenSecurity's turret direction convention.
  local yaw = (math.deg(atan2(-dx, -dz)) + 360) % 360
  local pitch = math.deg(atan2(dy, horizontal))
  return turret.moveTo(yaw, pitch)
end

local function run()
  assert(turret.powerOn())
  assert(turret.setArmed(true))
  print("Tracking the nearest Zombie. Press Ctrl+C to stop.")

  while true do
    local entities, reason = detector.scanEntities(SCAN_RANGE)
    if type(entities) ~= "table" then
      io.stderr:write("Detector scan failed: " .. tostring(reason) .. "\n")
    else
      local target = nearestTarget(entities)
      if target then
        local moved, moveReason = aimAt(target)
        if not moved then
          io.stderr:write("Turret aim failed: " .. tostring(moveReason) .. "\n")
        else
          local onTarget = turret.isOnTarget()
          if onTarget and turret.isReady() then
            local fired, fireReason = turret.fire()
            if not fired then
              io.stderr:write("Turret fire failed: " .. tostring(fireReason) .. "\n")
            end
          end
        end
      end
    end
    computer.pullSignal(SCAN_DELAY)
  end
end

local ok, reason = xpcall(run, debug.traceback)
pcall(turret.setArmed, false)
pcall(turret.powerOff)
if not ok then
  io.stderr:write(tostring(reason) .. "\n")
end
