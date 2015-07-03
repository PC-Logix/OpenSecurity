local keyboard = require("keyboard")
local computer = require("computer")
local fs = require("filesystem")
local event = require("event")
local shell = require("shell")
local auth = require("auth")
local term = require("term")

local running = true

local function check() -- Prevents "ctrl+alt+c" and "ctrl+c".
 if keyboard.isControlDown() then
  io.stderr:write("( ͡° ͜ʖ ͡°)")
  os.sleep(0.1)
  computer.shutdown(true)
 end
end
event.listen("key_down", check)

while running do
  if fs.exists("/installer.lua") then -- Auto deletes the installer at first boot.
    fs.remove("/installer.lua")
  end

  term.clear()
  term.setCursor(1,1)
  print(_OSVERSION .. " " .. os.date("%F %T"))
  term.write("User: ")
  username = term.read()
  username = string.gsub(username, "\n", "")
  username = string.lower(username)
  term.setCursor(1,3)
  term.write("Password: ")
  password = term.read(nil, nil, nil, "")
  password = string.gsub(password, "\n", "")

  login = auth.validate(username, password)

  if login then
    auth.userLog(username, "pass")
    if fs.get("/tmp/").isReadOnly() then
      return
    else
    hn = io.open("/tmp/.hostname.dat", "w") -- Writes the user inputted username to file for future use.
     hn:write(username)
      hn:close()
    end
    term.clear()
    term.setCursor(1,1)
    print("Welcome, " ..username)
    os.sleep(1.5)
    term.clear()
    term.setCursor(1,1)
    os.setenv("PS1", username .. "@" .. username .. "# ") -- Sets the user environment.
    shell.setWorkingDirectory("/home/" .. username .. "/")
    if not fs.get("/").isReadOnly() then
      shell.execute("/root/.root.lua/") -- Starts the root check program.
    end
    username, password = "" -- This is just a "bandaid fix" till I find a better way of doing it.
    if fs.isAutorunEnabled() == false then
      fs.setAutorunEnabled(true)
    end
    event.ignore("key_down", check)
    running = false
  else
    auth.userLog(username, "fail")
    term.clear()
    term.setCursor(1,1)
    io.stderr:write("Login failed: Invalid information.")
    os.sleep(2.5)
  end
end
