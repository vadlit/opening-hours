# Opening-Hours
## Purpose
This is a backend server providing pretty formatted information about opening hours of a restaurant.

## How to run
The service uses the platform provided in a separated repository as they usually do: https://github.com/vadlit/platform

CI/CD is not provided.

To run the server locally: 
1. Open the platform project directory - https://github.com/vadlit/platform
2. Call `mvn clean install` in order to install the platform into your local m2 repository. More about mvn repositories - https://maven.apache.org/guides/introduction/introduction-to-repositories.html
3. Return to this project (`opening-hours`) and build it `mvn clean package`
4. Run the application: through IDE or in your terminal like any other Java/Kotlin application: `java -cp "opening-hours-1.0.0-SNAPSHOT.jar" -classpath "<YOUR CLASS PATH>" ru.vadlit.openinghours.OpeningHoursApplication`
5. Your server is ready in a few seconds!

## Destination
The server listens to 9500 port by default, but you can change it in the configuration.

## Errors
All request errors are provided in special JSON format.
They also contain templates like `A problem encountered. Details: {details}` which help to translate messages at frontend side easier.

## End points
The only REST API end-point of the server is /api/v1/format which returns pretty formatted opening hours for your input.

Request example:

```
curl --location --request GET 'http://localhost:9500/api/v1/format' \
--header 'Content-Type: application/json' \
--data-raw '{
  "monday": [
  ],
  "tuesday": [
    {
      "type": "open",
      "value": 36000
    },
    {
      "type": "close",
      "value": 64800
    }
  ],
  "wednesday": [
  ],
  "thursday": [
    {
      "type": "open",
      "value": 36000
    },
    {
      "type": "close",
      "value": 64800
    }
  ],
  "friday": [
    {
      "type": "open",
      "value": 36000
    }
  ],
  "saturday": [
    {
      "type": "close",
      "value": 3600
    },
    {
      "type": "open",
      "value": 36000
    }
  ],
  "sunday": [
    {
      "type": "close",
      "value": 3600
    },
    {
      "type": "open",
      "value": 43200
    },
    {
      "type": "close",
      "value": 75600
    }
  ]
}'
```

Response example:

```
{
    "formatted_days": {
        "monday": "Closed",
        "tuesday": "10 AM - 6 PM",
        "wednesday": "Closed",
        "thursday": "10 AM - 6 PM",
        "friday": "10 AM - 1 AM",
        "saturday": "10 AM - 1 AM",
        "sunday": "12 PM - 9 PM"
    }
}
```

Also, you can view a response in the server logs if your logging level is not lower than INFO. Example:
```
A restaurant is open:
Monday: Closed
Tuesday: 10 AM - 6 PM
Wednesday: Closed
Thursday: 10 AM - 6 PM
Friday: 10 AM - 1 AM
Saturday: 10 AM - 1 AM
Sunday: 12 PM - 9 PM
```

### Input explanation
Input JSON consist of keys indicating days of a week and corresponding opening hours as values. 
One JSON file includes data for one restaurant. 
```
{ 
<dayofweek>: <opening hours>
<dayofweek>: <opening hours>
... 
}
```
 `<dayofweek>`​:  ​monday / tuesday / wednesday / thursday / friday / saturday / sunday
 
 `<opening hours>`​: an array of objects containing opening hours. Each object consist of two keys:
 - `type`​: ​open ​or ​close
 - `value`​:​ opening / closing time as UNIX time (1.1.1970 as a date), e.g. ​32400 = 9 AM, ​37800 = 10.30 AM, max value is​ 86399 = 11.59:59 PM
 
Example: on Mondays a restaurant is open from 9 AM to 8 PM
```
{
   "monday": [
      {
         "type":"open",
         "value":32400
      },
      {
         "type":"close",
         "value":72000
      }
   ]
}
```

### Possible improvements
JSON format of requests to receive such a kind of information is probably not the best decision.

Cons:
- It's easy to make a mistake and, as an instance, provide two schedules for the same day of week in one request. In this case server will use the last schedule only.
- The structure of this JSON could be simplier:
    - If 'open'/'close' states are the only possible ones, and they alternate always, then what's stopping us to wait for a simple array [`open_time`,`close_time`, ...] as a day schedule? Also, such an approach would let us avoid mistakes when a client sends a request with invalid states changes such as 'close -> close'.
    - What for do we call days if they are always the same? We could use a fixed array of 7 items - and our JSON would be shorter. The only disadvantage here - we would lose a possibility to not mention a particular day at all. Probably a special fake time like `-1` would help us.
    ```
    [
        [`open_time`,`close_time`, ...],
        [],
        [`open_time`,`close_time`,`open_time`],
        [`close_time`,`open_time`,`close_time`],
        [],
        [-1],
        [`open_time`,`close_time`, ...]  
    ]
    ```    
    - If you very want to specify the days explicitly you could use shortened day names like FRI,SAT or their indices (1,2,...,7) instead of long names
    - I don't know who are the clients, but probably it would be easier for them to provide time not in UNIX-format but in a human readable one. Something like "10 AM" instead of 36000. As for me, 24h format with fixed sizes would be even simpler.
    ```
        [
            ['Closed'],
            [], // means the day is not mentioned
            ['09:00', '18:30', '19:30'],
            ['01:00', '09:00', '18:30'],
            ['09:00', '18:30'],
            ['09:00', '18:30'],
            ['09:00', '18:30']  
        ]
        ```    
- JSON requests are heavy as well as other HTTP requests. I prefer gRPC. Frankly I don't think that it's matter in this particular case - hardly someone would call such a server under high load.

Also, you can provide a schedule for an unknown day of week because of a type: "tusday", for example. In this case server will ignore that schedule. To be honest, I could change such a behavior using special parameters of the JSON deserializer I used, but usually I don't do it because it would mean problems in a backward compatibility in further server improvements. I think it's better when your obsolete server is ready for requests from a more fresh client which may contain some new optional parameters.

If I used gRPC I wouldn't have any problems with possible typos - protocol and its parameters are defined there through proto-definitions which lets me use the fields that are supported in the particular API version only.

In addition, I believe it's not the very best decision to use a standalone service for such a small task. There's no any state, and the logic is super simple - it should be written as a part of another service.
