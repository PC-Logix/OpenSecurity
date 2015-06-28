local string = require("string")
local auth = require("auth")
local term = require("term")

hn = io.open("/tmp/.hostname.dat", "r")
 texthn = hn:read()
  hn:close()

local _, super = auth.validate(texthn, "*****")

term.clear()
term.setCursor(1,1)
term.write("Changing password for ".. texthn ..". \n")
term.write("Current password: ")
passwordOld = term.read(nil, nil, nil, "")
passwordOld = string.gsub(passwordOld, "\n", "")
term.setCursor(1,3)
term.write("New password: ")
passwordNew1 = term.read(nil, nil, nil, "")
passwordNew1 = string.gsub(passwordNew1, "\n", "")
term.setCursor(1,4)
term.write("Retype new password: ")
passwordNew2 = term.read(nil, nil, nil, "")
passwordNew2 = string.gsub(passwordNew2, "\n", "")
term.setCursor(1,5)

  if super == true then
    su = true
  else
    su = false
  end

if auth.validate(texthn, passwordOld) == true and passwordNew1 == passwordNew2 then
  auth.addUser(texthn, passwordNew2, su)
  term.write("passwd: password updated successfully \n")
  return
else
  term.write("passwd: password not successfully updated \n")
  return
end
