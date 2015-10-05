-- Fixed with the help of DrHoffman from IRC.

local shell = require("shell")
local auth = require("auth")
local term = require("term")

local hn = io.open("/tmp/.hostname.dat", "r") -- Reads the hostname file.
  texthn = hn:read()
    hn:close()

local function request()
  term.write("[sudo] password for ".. texthn ..": ")
    password = term.read(nil, nil, nil, "")
    password = string.gsub(password, "\n", "")
end

local args = {...}

if #args ~= 0 then
  path = args[1]
else
  io.stderr:write("error") -- Too lazy to properly do this bit atm...
end

request()

login, super = auth.validate(texthn, password)

if login and super then
  auth.userLog(texthn, "root_pass")
  local r = io.open("/tmp/.root", "w")
    r:write("true")
      r:close()
  username, password = "" -- This is just a "bandaid fix" till I find a better way of doing it.
  os.sleep(0.1)
  local result, reason = shell.execute(path, nil, table.unpack(args,2))
  if not result then
    io.stderr:write(reason)
  end
  os.sleep(0.1)
  if args[1] == "su" then
    return
  else
    os.remove("/tmp/.root")
  end
else
  auth.userLog(texthn, "root_fail")
  io.write("Sorry, try again. \n")
  username, password = "" -- This is just a "bandaid fix" till I find a better way of doing it.
end
