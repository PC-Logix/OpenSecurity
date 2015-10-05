local string = require("string")

local debug = {}

function debug.benchmark(amt) -- Not a clue what it could really be used for, but here it is.
  local x = os.clock()
  local s = 0
  for i=1,(amt or 100000) do
    s = s + i
  end
    return string.format("elapsed time: %.2f", os.clock() - x)
end

function debug.coinToss(times)
  local heads = 0
  local tails = 0
    for flips = 1, (times or 1) do
      coin = math.random(2)
      if coin == 1 then
        heads = heads + 1
      else
        tails = tails + 1
      end
    end
      return heads, tails
end

function debug.diceRoll(amt,sides,mod)
  return (amt or 1) * (math.random(sides or 6) + (mod or 0))
end

return debug
