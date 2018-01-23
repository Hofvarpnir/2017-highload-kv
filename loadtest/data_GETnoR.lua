wrk.method = "GET"
wrk.port = 8080
wrk.host = "http://localhost"
request_id = 0

request = function()
   path = "/v0/"
   request_id  = request_id + 1
   body = "Carol Malus Dienheim"
   url = wrk.format("PUT", path.."entity?id="..request_id, nil, body)

   return url
end


-- function response(status, headers, body)
--    print(status)
--    print(headers)
--    print(body)
-- end