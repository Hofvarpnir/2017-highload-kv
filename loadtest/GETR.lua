wrk.method = "GET"
wrk.port = 8080
wrk.host = "http://localhost"


ids = {}

request = function()
  path = "/v0/"
  gid = math.random( 30 );
  url = wrk.format("GET", path.."entity?id="..gid)

   -- print(url)
   return url
end


-- function response(status, headers, body)
--    print(status)
--    print(headers)
--    print(body)
-- end