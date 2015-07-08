local fs = require("filesystem")
local term = require("term")
local auth = require("auth")

local function root()
  local root = false
  if require("filesystem").exists("/tmp/.root") then
    local r = io.open("/tmp/.root", "r")
     root = r:read()
      r:close()
  end
  return root
end

local root = root()

if not root then
  io.stderr:write("not authorized")
  return
end

local args = {...}

if #args == 0 then
  term.clear()
  term.setCursor(1,1)
  term.write("Please enter a username to delete from the system.")
  term.setCursor(1,2)
  term.write("Username: ")
    username = term.read()
    username = string.gsub(username, "\n", "")
    username = string.lower(username)

  auth.rmUser(username)

  if fs.exists("/home/" .. username .. "/") then
      fs.remove("/home/" .. username .. "/")
  end

  username = ""

end

if #args == 1 then
  username = args[1]
  auth.rmUser(username)
  if fs.exists("/home/" .. username .. "/") then
    fs.remove("/home/" .. username .. "/")
  end
  username = ""
end
