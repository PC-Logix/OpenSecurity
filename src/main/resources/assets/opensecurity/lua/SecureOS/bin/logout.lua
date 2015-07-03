local fs = require("filesystem")
local shell = require("shell")
local event = require("event")

k = io.open("/tmp/.key", "r")
 textk = k:read()
  k:close()

  event.cancel(tonumber(textk))

if fs.isAutorunEnabled() == true then
  fs.setAutorunEnabled(false)
end

fs.remove("/tmp/.root")
shell.setWorkingDirectory("/")
shell.execute("/boot/99_login.lua")
