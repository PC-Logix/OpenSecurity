local computer = require("computer")
local shell = require("shell")

if not require("auth").isRoot() then
  io.stderr:write("not authorized")
  return
end

local args = shell.parse(...)
if #args < 1 then
  io.write("Usage: useradd <name>")
  return
end

local result, reason = computer.addUser(args[1])
if not result then
  io.stderr:write(reason)
end
