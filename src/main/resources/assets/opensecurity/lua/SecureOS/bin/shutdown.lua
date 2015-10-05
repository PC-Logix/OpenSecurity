local computer = require("computer")
local shell = require("shell")
local term = require("term")

local args, options = shell.parse(...)

if options.r then
  timeType = " minutes "
  mode = "rebooting "
  if args[1] == "now" then
    args[1] = "0"
  end
  if args[1] == nil then
    args[1] = "0"
  end
  time = tonumber(args[1]) * 60
else
  mode = "shutting down "
end

if #args >= 1 then
  if args[1] == "now" then
    timeType = "now"
    time = 0
  elseif options.s then
    timeType = " seconds "
    time = tonumber(args[1])
  else
    timeType = " minutes "
    time = tonumber(args[1]) * 60
  end

  if args[2] ~= nil then
    message = "System " .. mode .. "in " .. tostring(args[1]) .. timeType .. "for " .. args[2] .. "."
  end
else
  print("shutdown [-rs] <time> [reason]")
  print(" -r reboot")
  print(" -s seconds")
  print(" <time> in minutes unless -s or 'now'")
  print(" [reason] reason for shutdown or reboot")
  return
end

local function system()
  if options.r then
    print(message)
    os.sleep(time)
    term.clear()
    os.remove("/tmp/.root")
    computer.shutdown(true)
  else
    print(message)
    os.sleep(time)
    term.clear()
    os.remove("/tmp/.root")
    computer.shutdown()
  end
end

system()
