local component = require("component")


local datablock = {}
-------------------------------------------------------------------------------
-- Converts binary data into hexadecimal string.
function datablock.toHex(data)
  return (data:gsub('.', function (c)
    return string.format('%02X', string.byte(c))
    end))
end

-- Converts hexadecimal string into binary data.
function datablock.fromHex(hex)
  return (data:gsub('..', function (cc)
    return string.char(tonumber(cc, 16))
    end))
end

-- Applies base64 encoding.
function datablock.encode64(data)
  return component.os_datablock.encode64(data)
end

-- Applies base64 decoding.
function datablock.decode64(data)
  return component.os_datablock.decode64(data)
end

--Applies rot13 to the data.
function datablock.rot13(data)
  return component.os_datablock.rot13(data)
end

-- Returns raw/binary SHA2-256 hash of data. Common form of presenting SHA is hexadecimal string, see data.toHex.
function datablock.sha256(data)
  return component.os_datablock.sha256(data)
end

-- Returns raw/binary MD5 hash of data. Common form of presenting SHA is hexadecimal string, see data.toHex.
function datablock.md5(data)
  return component.os_datablock.md5(data)
end

-- Returns raw/binary CRC-32 hash of data. Common form of presenting SHA is hexadecimal string, see data.toHex.
function datablock.crc32(data)
  return component.os_datablock.crc32(data)
end


-- Applies DEFLATE compression.
function datablock.deflate(data)
  return component.os_datablock.deflate(data)
end

-- Applies INFLATE decompression.
function datablock.inflate(data)
  return component.os_datablock.inflate(data)
end

return datablock