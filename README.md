# Hotel Service: Spray 

## How to run the service
Clone the repository:
```
> git clone https://github.com/kamolchanokTa/HotelService.git
```

Get to the `HotelService` folder:
```
> cd spray-akka
```

Run the service:
```
> sbt run
```

The service runs on port 5001 by default.

### Serach Hotels By city
```
curl -v http://localhost:5001/hotels/{city}
```
{city} = Bangkok

### Sort Price Hotel (ASC/DESC)
```
curl -v http://localhost:5001/sort/ASC
```

### Get a Hotel By hotelId
```
curl -v http://localhost:5001/hotel/2
```