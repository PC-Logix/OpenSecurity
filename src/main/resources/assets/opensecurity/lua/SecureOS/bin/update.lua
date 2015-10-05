local component = require("component")
local computer = require("computer")
local shell = require("shell")
local term = require("term")

if not require("auth").isRoot() then
  io.stderr:write("not authorized")
  return
end

local args, options = shell.parse(...)

if not component.isAvailable("internet") then
  io.stderr:write("No internet card found.")
  return
end

local function update(args, options)

  if #args == 0 then
    u = io.open("/etc/update.cfg", "r")
      textu = u:read()
        u:close()
  end

  if #args == 1 then
    textu = args[1]
    textuw = io.open("/tmp/update-tmp.cfg", "w")
      textuw:write(textu)
        textuw:close()
    if textu ~= "dev" and textu ~= "release" then
      io.stderr:write("Not a vaild repo tree.")
      return
    end
  end

  if options.a then
    uw = io.open("/etc/update.cfg", "w")
      uw:write(tostring(args[1]))
        uw:close()
  end

shell.execute("wget -fq https://raw.githubusercontent.com/Shuudoushi/SecureOS/" .. textu .. "/tmp/update-tmp.lua /tmp/update-tmp.lua")
term.clear()
term.setCursor(1,1)
print("SecureOS will now update from " .. textu .. ".")
  os.sleep(1)
  shell.execute("wget -fq https://raw.githubusercontent.com/Shuudoushi/SecureOS/" .. textu .. "/tmp/depreciated.dat /tmp/depreciated.dat \n")
  print("Checking for depreciated packages.")

  local function depreciated()
    local env = {}
    local config = loadfile("/tmp/depreciated.dat", nil, env)
    if config then
      pcall(config)
    end
    return env.depreciated
  end

  local depreciated = depreciated()

  if depreciated then
    for i = 1, #depreciated do
      local files = os.remove(shell.resolve(depreciated[i]))
      if files ~= nil then
        print("Removed " .. depreciated[i] .. ": a depreciated package")
      end
    end
    print("Finished")
  end

shell.execute("/tmp/update-tmp.lua")
os.remove("/tmp/update-tmp.lua")
os.remove("/tmp/update-tmp.cfg")
require("auth").userLog(os.getenv("USER"), "update")
term.clear()
term.setCursor(1,1)
print("Update complete. System restarting now.")
  os.sleep(2.5)
  os.remove("/tmp/.root")
  computer.shutdown(true)
end

update(args, options)
