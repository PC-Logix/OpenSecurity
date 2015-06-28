local shell = require("shell")
local event = require("event")

k = io.open("/tmp/.key", "r")
 textk = k:read()
  k:close()

  event.cancel(tonumber(textk))

shell.setWorkingDirectory("/")
shell.execute("/boot/99_login.lua")