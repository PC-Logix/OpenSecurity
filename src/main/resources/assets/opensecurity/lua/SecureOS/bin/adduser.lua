local fs = require("filesystem")
local term = require("term")
local auth = require("auth")

if not require("auth").isRoot() then
  io.stderr:write("not authorized")
  return
end

local function dirTree(username)
  if not fs.exists("/home/" .. username .. "/") then
    fs.makeDirectory("/home/" .. username .. "/")
    fs.makeDirectory("/home/" .. username .. "/bin/")
    fs.makeDirectory("/home/" .. username .. "/lib/")
    fs.makeDirectory("/home/" .. username .. "/var/")
  end
end

local args = {...}

if #args >= 2 then
  username = args[1]
  password = args[2]

  if args[3] == "true" then
    su = true
  elseif args[3] == "false" then
    su = false
  elseif args[3] == nil then
    su = false
  else io.stderr:write("Invalid.")
    return
  end

  auth.addUser(args[1], args[2], su)
  dirTree(username)
  auth.userLog(username, "added")
  print(username .. " added")
  username, password, su = ""
  return
else
  term.clear()
  term.setCursor(1,1)
  term.write("Please enter a username and password to add to the system. Usernames must be lowercase.")
  term.setCursor(1,2)
  term.write("Username: ")
    username = term.read()
    username = string.gsub(username, "\n", "")
    username = string.lower(username)
  term.setCursor(1,3)
  term.write("Password: ")
    password = term.read(nil, nil, nil, "")
    password = string.gsub(password, "\n", "")
  term.setCursor(1,4)
  term.write("Root rights (Y/n): ")
    su = term.read()
    su = string.gsub(su, "\n", "")
    su = string.lower(su)

  if su == "y" or su == "yes" then
    su = true
  elseif su == "n" or su == "no" then
    su = false
  elseif su == nil then
    su = false
  else io.stderr:write("Invalid.")
    return
  end

  auth.addUser(username, password, su)

  dirTree(username)
  auth.userLog(username, "added")
  username, password, su = ""
end
