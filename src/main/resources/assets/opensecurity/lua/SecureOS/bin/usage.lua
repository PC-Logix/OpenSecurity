local component = require("component")
local computer = require("computer")
local fs = require("filesystem")
local shell = require("shell")
local text = require("text")

local function round(num, idp)
 local mult = 10^(idp or 0)
 return math.floor(num * mult + 0.5) / mult
end

local usedMemory = round(computer.totalMemory() / 1048576 - computer.freeMemory() / 1048576, 2)
local freeMem = round(computer.freeMemory() / 1048576, 2)

print("Date/Time: " .. os.date())
print("Up Time: " .. round(computer.uptime() / 60, 2) .. " Minutes")

if component.isAvailable("tablet") then
 print("Power: " .. round(computer.energy(), 2) .. "/" .. round(computer.maxEnergy(), 2))
end

print("Total Memory: " .. round(computer.totalMemory() / 1048576, 2) .. " MB")

if freeMem * 100 <= 15 then
 component.gpu.setForeground(0xFF0000)
  print("Free Memory: " .. round(freeMem, 2) .. " MB")
 component.gpu.setForeground(0xFFFFFF)
 else
  print("Free Memory: " .. round(freeMem, 2) .. " MB")
end

if usedMemory >= 85 then
 component.gpu.setForeground(0xFF0000)
  print("Used Memory: " .. round(usedMemory, 2) * 100 .. "%")
 component.gpu.setForeground(0xFFFFFF)
 else
  print("Used Memory: " .. round(usedMemory, 2) * 100 .. "%")
end

local args, options = shell.parse(...)

local function formatSize(size)
  if not options.h then
    return tostring(size)
  end
  local sizes = {"", "K", "M", "G"}
  local unit = 1
  local power = options.si and 1000 or 1024
  while size > power and unit < #sizes do
    unit = unit + 1
    size = size / power
  end
  return math.floor(size * 10) / 10 .. sizes[unit]
end

local mounts = {}
if #args == 0 then
  for proxy, path in fs.mounts() do
    mounts[path] = proxy
  end
else
  for i = 1, #args do
    local proxy, path = fs.get(args[i])
    if not proxy then
      io.stderr:write(args[i], ": no such file or directory\n")
    else
      mounts[path] = proxy
    end
  end
end

local result = {{"Filesystem", "Used", "Available", "Use%", "Mounted on"}}
for path, proxy in pairs(mounts) do
  local label = proxy.getLabel() or proxy.address
  local used, total = proxy.spaceUsed(), proxy.spaceTotal()
  local available, percent
  if total == math.huge then
    used = used or "N/A"
    available = "unlimited"
    percent = "0%"
  else
    available = total - used
    percent = used / total
    if percent ~= percent then -- NaN
      available = "N/A"
      percent = "N/A"
    else
      percent = math.ceil(percent * 100) .. "%"
    end
  end
  table.insert(result, {label, formatSize(used), formatSize(available), tostring(percent), path})
end

local m = {}
for _, row in ipairs(result) do
  for col, value in ipairs(row) do
    m[col] = math.max(m[col] or 1, value:len())
  end
end

for _, row in ipairs(result) do
  for col, value in ipairs(row) do
    io.write(text.padRight(value, m[col] + 2))
  end
  io.write("\n")
end