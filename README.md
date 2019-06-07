# Url Shortener

Just like Bitly, send a url get a shortened version back.

#### Dependencies
- [Java -v0.8](https://java.com/en/download/) 
- [Scala -v 2.12.2](https://www.scala-lang.org/download/)

#### Testing

```
sbt test
```

#### Run

```
sbt run 
```

#### Using the Api

---

**POST** /

request body
```json
{
  "url": "https://duncannevin.com"
}
```

201 
```json
{
  "shortenedUrl": "http://localhost:9000/asdf98",
  "hash": "asdf98",
  "url": "https://duncannevin.com"
}
```

**GET** /[hash]

301 Redirect to site
