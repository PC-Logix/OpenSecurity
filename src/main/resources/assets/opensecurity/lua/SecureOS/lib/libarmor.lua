local libarmor = {}

function libarmor.protect(original, whitelist)
  --create a protected proxy table
  local proxy = {}
  --redirecting next is the easiest way to create an iterator
  local function redirectedNext(t, k)
    return next(original, k)
  end
  local meta = {
    --reading access is unchanged
    __index = original,
    --writing access is only possible if the key is in the whitelist
    --You can create a blacklist by giving the whitelist a corresponding __index metamethod.
    __newindex = function(_, key, value)
      if whitelist and whitelist[key] then
        original[key] = value
      else
        error("Access denied: attempted to overwrite protected table")
      end
    end,
    --prohibits access to the metatable
    __metatable = "protected",
    --iteration without returning the protected table
    __pairs = function()
      return redirectedNext, proxy, nil
    end,
  }
  if _VERSION <= "Lua 5.2" then
    --ipairs metamethod needed for version 5.2 and lower
    --Higher versions respect the __index metamethods.
    local function redirectedINext(t, k)
      k = k + 1
      --Lua 5.2 behaviour: rawget
      local value = rawget(original, k)
      if value ~= nil then
        return k, value
      end
    end
    function meta.__ipairs()
      return redirectedINext, proxy, 0
    end
  end
  return setmetatable(proxy, meta)
end

return libarmor.protect(libarmor)
