--Some of this was made with the help of SuPeRMiNoR2
--Or maybe all of it

passwdfile = "/etc/passwd"

local auth = {}
local component = require("component")
local fs = require("filesystem")
local sha = require("sha256")
local libarmor = require("libarmor")


if not fs.exists(passwdfile) then
  f = io.open(passwdfile, "w")
  f:write()
  f:close()
end

local function split(str,sep)
  local array = {}
  local reg = string.format("([^%s]+)",sep)
  for mem in string.gmatch(str,reg) do
    table.insert(array, mem)
  end
  return array
end

local function buildDB()
  local users = {}
  u = io.open(passwdfile, "r")
  raw = u:read("*a")

  if raw ~= nil then

    local temp = split(raw, "\n")

    for _,data in pairs(temp) do
      t = split(data, ":")
      users[t[1]] = {password=t[2], su=t[3]}
    end

  end

  return users
end

local function saveDB(db)
  local buff = ""
  for u, d in pairs(db) do
    buff = buff .. u .. ":" .. d["password"].. ":" .. d["su"] .. "\n"
  end
  f = io.open(passwdfile, "w")
  f:write(buff)
  f:close()
end

function auth.addUser(username, password, su)
  local users = buildDB()
  if su == true then sub = "1" end
  if su == false then sub = "0" end

  users[username] = {password=sha.sha256(password), su=sub}
  saveDB(users)
end

function auth.rmUser(username)
  local users = buildDB()
  for user,_ in pairs(users) do
    if user == username then
      users[username] = nil
    end
  end
  saveDB(users)
end

function auth.validate(username, password)
  local users = buildDB()

  validated = false
  superuser = false

  local data = users[username]

  if data ~= nil then
    if data["password"] == sha.sha256(password) then
      validated = true
    end
    if data["su"] == "1" then
      superuser = true
    end
  end
  return validated, superuser
end

--Shection (Shuu+Section)

function auth.userLog(username, arg)
  if not fs.get("/").isReadOnly() then
  if not fs.exists("/var/log/") then
    fs.makeDirectory("/var/log/")
  end
  userw = io.open("/var/log/auth.log", "a")
  userw:write(username .. "|" .. os.date("%F %X") .. "|" .. arg .. "\n")
  userw:close()
  end
end

function auth.isRoot()
  local root = false
  if fs.exists("/tmp/.root") then
    local r = io.open("/tmp/.root", "r")
    root = r:read()
    r:close()
  end
  return root
end

return libarmor.protect(auth)
