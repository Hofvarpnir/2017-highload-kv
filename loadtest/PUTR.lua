wrk.method = "PUT"
wrk.port = 8080
wrk.host = "http://localhost"
local request_id = 0



request = function()
   path = "/v0/"
   request_id  = request_id + 1
   body = "Carol Malus Dienheim"
   i = (request_id % 4)+1
   url = wrk.format("PUT", path.."entity?id="..i, nil, body)
   return url
end


-- function response(status, headers, body)
   
-- end