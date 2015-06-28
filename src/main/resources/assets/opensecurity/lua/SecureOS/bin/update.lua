local component = require("component")
local computer = require("computer")
local shell = require("shell")
local term = require("term")

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

term.clear()
term.setCursor(1,1)
print("SecureOS will now update.")
    os.sleep(1)
    term.setCursor(1,2)
shell.execute("wget -f https://raw.githubusercontent.com/Shuudoushi/SecureOS/" .. textu .. "/tmp/update-tmp.lua /tmp/update-tmp.lua \n")
shell.execute("/tmp/update-tmp.lua")
shell.execute("rm /tmp/update-tmp.lua")
shell.execute("rm /tmp/update-tmp.cfg")
term.clear()
term.setCursor(1,1)
print("Update complete. System restarting now.")
    os.sleep(2.5)
    computer.shutdown(true)
end

update(args, options)
