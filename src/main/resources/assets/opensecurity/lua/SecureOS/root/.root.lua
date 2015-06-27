local event = require("event")
local shell = require("shell")
local term = require("term")
local function root()
 hn = io.open("/tmp/.hostname.dat", "r")
  texthn = hn:read()
   hn:close()
 local dir = shell.getWorkingDirectory()
 local userHome = "/home/" .. texthn .. "/"
if dir ~= userHome and dir <= userHome then
 io.stderr:write("This action requires root access.")
  term.clear()
  term.setCursor(1,1)
  shell.execute("/root/sudo.lua")
else
 k = io.open("/tmp/.key", "w")
  k:write(timer_id)
   k:close()
 end
end
timer_id = event.timer(1, root, math.huge)