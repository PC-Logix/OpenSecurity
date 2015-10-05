local computer = require("computer")
local shell = require("shell")

if not require("auth").isRoot() then
  io.stderr:write("not authorized")
  return
end

local args = shell.parse(...)
if #args < 1 then
  io.write("Usage: userdel <name>")
  return
end

if not computer.removeUser(args[1]) then
  io.stderr:write("no such user")
end
