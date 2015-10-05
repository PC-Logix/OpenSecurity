local computer = require("computer")
local fs = require("filesystem")
local kb = require("keyboard")
local event = require("event")
local shell = require("shell")
local term = require("term")

local running = true

local function greeting()
term.clear()
term.setCursor(1,1)
  print("Welcome to the SecureOS installer!")
  print("This installer will help guide you through setting up and installing SecureOS.")
  print("Press any key to continue.")
local event = event.pull("key_down")
  if event and not kb.isControlDown() then
    term.clear()
    term.setCursor(1,1)
  end
end

local function downLoad()

  if not fs.exists("/tmp/.root") then
    r = io.open("/tmp/.root", "w")
     r:write("true")
      r:close()
  end

  if not fs.exists("/root/") then
    fs.makeDirectory("/root/")
  end

  if not fs.exists("/etc/update.cfg") then
    c = io.open("/etc/update.cfg", "w")
     c:write("release")
      c:close()
  end

  term.write("Please wait while the files are downloaded and installed.")
  os.sleep(1)
  term.setCursor(1,2)

shell.execute("wget https://raw.githubusercontent.com/Shuudoushi/SecureOS/release/bin/update.lua /bin/update.lua \n")
shell.execute("/bin/update.lua")
end

local function userInfo()
  term.clear()
  term.setCursor(1,1)
  term.write("Please enter a username and password. Usernames must be lowercase.")
  term.setCursor(1,2)
  term.write("Username: ")
    username = term.read()
    username = string.gsub(username, "\n", "")
    username = string.lower(username)
  term.setCursor(1,3)
  term.write("Password: ")
    password = term.read(nil, nil, nil, "")
    password = string.gsub(password, "\n", "")

  require("auth").addUser(username, password, true)

  if not fs.exists("/home/" .. username .. "/") then
    fs.makeDirectory("/home/" .. username .. "/")
    fs.makeDirectory("/home/" .. username .. "/bin/")
    fs.makeDirectory("/home/" .. username .. "/lib/")
    fs.makeDirectory("/home/" .. username .. "/var/")
  end

  term.clear()
  term.setCursor(1,1)
  term.write("Would you like to restart now? [Y/n]")
  term.setCursor(1,2)
    input = term.read()
    input = string.gsub(input, "\n", "")
    input = string.lower(input)

    if input == "y" or input == "yes" then
      computer.shutdown(true)
    elseif input == "n" or input == "no" then
      io.write("Returning to shell.")
      os.sleep(1.5)
      term.clear()
      term.setCursor(1,1)
      running = false
    end
end

while running do
    greeting()
    downLoad()
    userInfo()
end
