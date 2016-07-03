#Encrypted Key Service

## Thanks to Scala REST API seed project: https://github.com/adrianhurt/play-api-rest-seed

-- on Play + Anorm + H2

There are two kinds of api users:
-- Admin users (Master control, need to login through signin entry point, token expire in 10 min)
-- Normal API client (can only access key related readable APIs, token expire in 10 years)

************************************************************************************************************************

                                                        Deployment

************************************************************************************************************************

-- Package generation
Configure the encription key and salt under app/models/Encryption, to avoid anyone able to hack into database

User activator, go to home folder and do
Unix: 
./activator universal:package-bin
Windows:
activator.bat universal:package-bin
then you will find the package under
/target/universal/key_service-1.0.zip

upload the zip file to the server and unzip to the folder you would like to place.

-- Configuration
configure file is under conf folder, you need to configure the location to store the db files under application.conf
b.default.url="jdbc:h2:file:{location of db file}"
configuration of log file is logback.xml

-- Run
go to bin folder under the unzipped folder, if http.port not specified, 9000 is the default
./key_service -Dhttp.port={port}
open browser to reach the root location of the website, website will prompt to create database for the first time.
click on execute the script, then you are ready to go



************************************************************************************************************************

                                                        Admin API

************************************************************************************************************************


####################################################################################################
                                                Login
####################################################################################################
-- Description
Admin Login using username and password and API key allocated to get a token for subsequent queries, use token to passing password every time to avoid password leakage

-- Location
/signin

-- Method
POST

-- Headers
X-Api-Key:{String}
Content-Type:application/json

-- Data Posted
{
    "email":{String},
    "password":{String}
}

-- Return
{
    "token":{String},
    "minutes":10
}

-- Example
curl -i -XPOST http://localhost:9000/signin -d '{"email":"xxx@xxx.com","password":"xxx"}' -H "X-Api-Key:xxx" -H "Content-Type:application/json"
> {"token":"ba954ad1-1e2c-4c42-9806-5a18dd85baf8","minutes":10}

####################################################################################################
                                                Logout
####################################################################################################
-- Description
Admin logout to delete the temporary token

-- Location
/signout

-- Method
POST

-- Headers
X-Api-Key:{String}
X-Auth-Token:{String}
Content-Type:application/json

-- Data Posted


-- Return
204 No Content

-- Example
curl -i -XPOST http://localhost:9000/signout -H "X-Auth-Token:631158af-8c57-45d3-a528-9b66e444af31" -H "X-Api-Key:XXX" -H "Content-Type:application/json"
HTTP/1.1 204 No Content
Date: Sat, 07 May 2016 16:07:42 GMT
Content-Language: en
Access-Control-Allow-Origin: *
Access-Control-Allow-Headers: Origin
Access-Control-Allow-Methods: GET, POST, PATCH, PUT, DELETE, OPTIONS

####################################################################################################
                                                SignUp
####################################################################################################
-- Description
Admin create new admin accounts

-- Location
/signup

-- Method
POST

-- Headers
X-Api-Key:{String}
X-Auth-Token:{String}
Content-Type:application/json

-- Data Posted
{
    "email":{String},
    "password":{String},
    "user":{
        "name":{String}// username
     }
}

-- Return
{
    "apiKey":{String},
    "token"{String},
    "minutes":10
}

-- Example
curl -i -XPOST http://localhost:9000/signup -d '{"email":"user1@mail.com","password":"123456","user":{"name":"User 4"}}' -H "X-Auth-Token:631158af-8c57-45d3-a528-9b66e444af31" -H "X-Api-Key:XXX" -H "Content-Type:application/json"
>{"apiKey":"User 4","token":"40d414db-7c20-4477-82e2-4628896c17ae","minutes":10}

####################################################################################################
                                                Account List
####################################################################################################
-- Description
Admin List Admin Accounts

-- Location
/account/list

-- Method
GET

-- Headers
X-Api-Key:{String}
X-Auth-Token:{String}
Content-Type:application/json

-- Params

-- Return
[
    {
        "id":{Long},
        "email":{String},
        "name":{String}
    }
    ...
]

-- Example
curl -i -XGET http://localhost:9000/account/list -H "X-Auth-Token:173984d9-e0be-49ac-bdca-6ef5adf4ff68" -H "X-Api-Key:XXX" -H "Content-Type:application/json"
HTTP/1.1 200 OK
Date: Sat, 07 May 2016 16:16:32 GMT
Content-Type: application/json; charset=utf-8
Content-Language: en
Access-Control-Allow-Origin: *
Access-Control-Allow-Headers: Origin
Access-Control-Allow-Methods: GET, POST, PATCH, PUT, DELETE, OPTIONS
Content-Length: 116

[{"id":1,"email":"xxx@xxx.com","name":"XXX"},{"id":33,"email":"user4@mail.com","name":"User 4"}]


####################################################################################################
                                                Update Name
####################################################################################################
-- Description
Admin Update user name

-- Location
/account

-- Method
PUT

-- Headers
X-Api-Key:{String}
X-Auth-Token:{String}
Content-Type:application/json

-- Post Data
{
    "name":{String}
}

-- Return
204 No Content

-- Example
curl -i -XPUT http://localhost:9000/account -d '{"name":"TEST"}' -H "X-Auth-Token:173984d9-e0be-49ac-bdca-6ef5adf4ff68" -H "X-Api-Key:XXX" -H "Content-Type:application/json"
HTTP/1.1 204 No Content
Date: Sat, 07 May 2016 16:20:27 GMT
Content-Language: en
Access-Control-Allow-Origin: *
Access-Control-Allow-Headers: Origin
Access-Control-Allow-Methods: GET, POST, PATCH, PUT, DELETE, OPTIONS

####################################################################################################
                                                Update Password
####################################################################################################
-- Description
Admin Update user password

-- Location
/account/password

-- Method
PUT

-- Headers
X-Api-Key:{String}
X-Auth-Token:{String}
Content-Type:application/json

-- Post Data
{
    "old":{String}, //old password
    "new":{String} //new password
}

-- Return
204 No Content

-- Example
curl -i -XPUT http://localhost:9000/account/password -d '{"old":"123456","new":"password"}' -H "X-Auth-Token:c3642376-6f20-4538-b386-6e404fea84a0" -H "X-Api-Key:XXX" -H "Content-Type:application/json"
HTTP/1.1 204 No Content
Date: Sat, 07 May 2016 16:27:41 GMT
Content-Language: en
Access-Control-Allow-Origin: *
Access-Control-Allow-Headers: Origin
Access-Control-Allow-Methods: GET, POST, PATCH, PUT, DELETE, OPTIONS


####################################################################################################
                                                Delete Account
####################################################################################################
-- Description
Admin delete an account

-- Location
/account/{id}

-- Method
DELETE

-- Headers
X-Api-Key:{String}
X-Auth-Token:{String}
Content-Type:application/json

-- Post Data

-- Return
204 No Content

-- Example
curl -i -XDELETE http://localhost:9000/account/1 -H "X-Auth-Token:c3642376-6f20-4538-b386-6e404fea84a0" -H "X-Api-Key:XXX" -H "Content-Type:application/json"
HTTP/1.1 204 No Content
Date: Sat, 07 May 2016 16:27:41 GMT
Content-Language: en
Access-Control-Allow-Origin: *
Access-Control-Allow-Headers: Origin
Access-Control-Allow-Methods: GET, POST, PATCH, PUT, DELETE, OPTIONS


####################################################################################################
                                                Token List
####################################################################################################
-- Description
Admin List all the client api tokens

-- Location
/token

-- Method
GET

-- Headers
X-Api-Key:{String}
X-Auth-Token:{String}
Content-Type:application/json

-- Params

-- Return
[
    {
        "apiKey":{String},
        "token":{String},
        "expirationTime":{Long} //timestamp
    }
    ...
]

-- Example
curl -i -XGET http://localhost:9000/token -H "X-Auth-Token:c3642376-6f20-4538-b386-6e404fea84a0" -H "X-Api-Key:XXX" -H "Content-Type:application/json"
HTTP/1.1 200 OK
Date: Sat, 07 May 2016 16:33:56 GMT
Content-Type: application/json; charset=utf-8
Content-Language: en
Access-Control-Allow-Origin: *
Access-Control-Allow-Headers: Origin
Access-Control-Allow-Methods: GET, POST, PATCH, PUT, DELETE, OPTIONS
Content-Length: 99

[{"apiKey":"User 4","token":"40d414db-7c20-4477-82e2-4628896c17ae","expirationTime":1462638127739}]


####################################################################################################
                                              Create Token
####################################################################################################
-- Description
Admin create new client token

-- Location
/token

-- Method
PUT

-- Headers
X-Api-Key:{String}
X-Auth-Token:{String}
Content-Type:application/json

-- Post data
{
    "apiKey":{String}
}

-- Return
{
    "apiKey":{String},
    "token":{String},
    "years":100
}

-- Example
curl -i -XPUT http://localhost:9000/token -d '{"apiKey":"testclient"}' -H "X-Auth-Token:f87c6524-6eb4-4ad5-a87d-59532cfd34e4" -H "X-Api-Key:XXX" -H "Content-Type:application/json"
HTTP/1.1 200 OK
Date: Sat, 07 May 2016 16:40:22 GMT
Content-Type: application/json; charset=utf-8
Content-Language: en
Access-Control-Allow-Origin: *
Access-Control-Allow-Headers: Origin
Access-Control-Allow-Methods: GET, POST, PATCH, PUT, DELETE, OPTIONS
Content-Length: 82

{"apiKey":"testclient","token":"b2b01a52-3f47-46a4-a345-fb8934abb064","years":100}


####################################################################################################
                                              Delete Token
####################################################################################################
-- Description
Admin delete the client token

-- Location
/token

-- Method
DELETE

-- Headers
X-Api-Key:{String}
X-Auth-Token:{String}
Content-Type:application/json

-- Post data
{
    "apiKey":{String}
}

-- Return
204 no content

-- Example
curl -i -XDELETE http://localhost:9000/token -d '{"apiKey":"testclient"}' -H "X-Auth-Token:f87c6524-6eb4-4ad5-a87d-59532cfd34e4" -H "X-Api-Key:XXX" -H "Content-Type:application/json"
HTTP/1.1 204 No Content
Date: Sat, 07 May 2016 16:42:01 GMT
Content-Language: en
Access-Control-Allow-Origin: *
Access-Control-Allow-Headers: Origin
Access-Control-Allow-Methods: GET, POST, PATCH, PUT, DELETE, OPTIONS


####################################################################################################
                                              Add Key
####################################################################################################
-- Description
Admin Add new Key

-- Location
/key

-- Method
PUT

-- Headers
X-Api-Key:{String}
X-Auth-Token:{String}
Content-Type:application/json

-- Post data
{
    "key":{String}
}

-- Return
201 created

-- Example
curl -i -XPUT http://localhost:9000/key -d '{"key":"key version 1"}'  -H "X-Auth-Token:f87c6524-6eb4-4ad5-a87d-59532cfd34e4" -H "X-Api-Key:XXX" -H "Content-Type:application/json"
HTTP/1.1 201 Created
Date: Sat, 07 May 2016 16:47:54 GMT
Location: http://localhost:9000/key/1
Content-Language: en
Access-Control-Allow-Origin: *
Access-Control-Allow-Headers: Origin
Access-Control-Allow-Methods: GET, POST, PATCH, PUT, DELETE, OPTIONS
Content-Length: 0


####################################################################################################
                                              Update Key
####################################################################################################
-- Description
Admin Update existing Key

-- Location
/key/{id}

-- Method
POST

-- Headers
X-Api-Key:{String}
X-Auth-Token:{String}
Content-Type:application/json

-- Post data
{
    "key":{String}
}

-- Return
204 no content

-- Example
curl -i -XPOST http://localhost:9000/key/1 -d '{"key":"key version 1"}'  -H "X-Auth-Token:f87c6524-6eb4-4ad5-a87d-59532cfd34e4" -H "X-Api-Key:XXX" -H "Content-Type:application/json"
HTTP/1.1 204 no content
Date: Sat, 07 May 2016 16:47:54 GMT
Location: http://localhost:9000/key/1
Content-Language: en
Access-Control-Allow-Origin: *
Access-Control-Allow-Headers: Origin
Access-Control-Allow-Methods: GET, POST, PATCH, PUT, DELETE, OPTIONS
Content-Length: 0

####################################################################################################
                                              Delete Key
####################################################################################################
-- Description
Admin Delete existing Key

-- Location
/key/{id}

-- Method
DELETE

-- Headers
X-Api-Key:{String}
X-Auth-Token:{String}
Content-Type:application/json

-- Post data

-- Return
204 no content

-- Example
curl -i -XDELETE http://localhost:9000/key/1  -H "X-Auth-Token:f87c6524-6eb4-4ad5-a87d-59532cfd34e4" -H "X-Api-Key:XXX" -H "Content-Type:application/json"
HTTP/1.1 204 no content
Date: Sat, 07 May 2016 16:47:54 GMT
Location: http://localhost:9000/key/1
Content-Language: en
Access-Control-Allow-Origin: *
Access-Control-Allow-Headers: Origin
Access-Control-Allow-Methods: GET, POST, PATCH, PUT, DELETE, OPTIONS
Content-Length: 0






************************************************************************************************************************

                                                        Client API

************************************************************************************************************************

There is no need to sign in, the API key and token can be used for 10 years

API KEY used to identify API Client, Token treated as the password

####################################################################################################
                                              List Key
####################################################################################################
-- Description
List Key

-- Location
/key

-- Method
GET

-- Headers
X-Api-Key:{String}
X-Auth-Token:{String}
Content-Type:application/json

-- Params

-- Return
[
    {
        "id":{Long},
        "key":{String},
        "insertTime":{Long}
    }
    ...
]

-- Example
curl -i -XGET http://localhost:9000/key -H "X-Auth-Token:9b799576-f3c6-4405-81ef-f874abaddb6c" -H "X-Api-Key:XXX" -H "Content-Type:application/json"
HTTP/1.1 200 OK
Date: Sat, 07 May 2016 16:58:28 GMT
Content-Type: application/json; charset=utf-8
Content-Language: en
Access-Control-Allow-Origin: *
Access-Control-Allow-Headers: Origin
Access-Control-Allow-Methods: GET, POST, PATCH, PUT, DELETE, OPTIONS
Content-Length: 66

[{"id":1,"key":"key version update 1","insertTime":1462639674250}]


####################################################################################################
                                              Get Key
####################################################################################################
-- Description
Get specific Key by id

-- Location
/key/{id}

-- Method
GET

-- Headers
X-Api-Key:{String}
X-Auth-Token:{String}
Content-Type:application/json

-- Params

-- Return
{
    "id":{Long},
    "key":{String},
    "insertTime":{Long}
}

-- Example
curl -i -XGET http://localhost:9000/key/1 -H "X-Auth-Token:9b799576-f3c6-4405-81ef-f874abaddb6c" -H "X-Api-Key:XXX" -H "Content-Type:application/json"
HTTP/1.1 200 OK
Date: Sat, 07 May 2016 16:58:28 GMT
Content-Type: application/json; charset=utf-8
Content-Language: en
Access-Control-Allow-Origin: *
Access-Control-Allow-Headers: Origin
Access-Control-Allow-Methods: GET, POST, PATCH, PUT, DELETE, OPTIONS
Content-Length: 66

{"id":1,"key":"key version update 1","insertTime":1462639674250}


####################################################################################################
                                              Get Latest Key
####################################################################################################
-- Description
Get the latest key

-- Location
/latest

-- Method
GET

-- Headers
X-Api-Key:{String}
X-Auth-Token:{String}
Content-Type:application/json

-- Params

-- Return
{
    "id":{Long},
    "key":{String},
    "insertTime":{Long}
}

-- Example
curl -i -XGET http://localhost:9000/latestkey -H "X-Auth-Token:9b799576-f3c6-4405-81ef-f874abaddb6c" -H "X-Api-Key:XXX" -H "Content-Type:application/json"
HTTP/1.1 200 OK
Date: Sat, 07 May 2016 16:58:28 GMT
Content-Type: application/json; charset=utf-8
Content-Language: en
Access-Control-Allow-Origin: *
Access-Control-Allow-Headers: Origin
Access-Control-Allow-Methods: GET, POST, PATCH, PUT, DELETE, OPTIONS
Content-Length: 66

{"id":1,"key":"key version update 1","insertTime":1462639674250}


####################################################################################################
                                              Get Key for Date
####################################################################################################
-- Description
Get key for specific date

-- Location
/historykey/{timestamp}

-- Method
GET

-- Headers
X-Api-Key:{String}
X-Auth-Token:{String}
Content-Type:application/json

-- Params

-- Return
{
    "id":{Long},
    "key":{String},
    "insertTime":{Long}
}

-- Example
curl -i -XGET http://localhost:9000/historykey/1462639684250 -H "X-Auth-Token:305268db-0332-47b8-9293-bfdbfca74555" -H "X-Api-Key:XXX" -H "Content-Type:application/json"
HTTP/1.1 200 OK
Date: Sat, 07 May 2016 17:08:38 GMT
Content-Type: application/json; charset=utf-8
Content-Language: en
Access-Control-Allow-Origin: *
Access-Control-Allow-Headers: Origin
Access-Control-Allow-Methods: GET, POST, PATCH, PUT, DELETE, OPTIONS
Content-Length: 64

{"id":1,"key":"key version update 1","insertTime":1462639674250}



