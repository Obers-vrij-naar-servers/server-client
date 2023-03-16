Applicatieprotocol: Het applicatieprotocol is gebaseerd op HTTP en bestaat uit de volgende stappen:
1. Verbinding maken
   De client maakt verbinding met de server en stuurt een verzoek om een sessie te starten.
2. Sessie starten
   De server controleert of de client is geautoriseerd om toegang te krijgen tot de server en stuurt een
   reactie terug om de sessie te starten.
3. Bestand uploaden
   De client uploadt een bestand naar de server door middel van een POST-verzoek. Het verzoek bevat
   het bestand zelf, samen met metagegevens zoals bestandsnaam, grootte en checksum.
4. Bestand downloaden
   De client downloadt een bestamnd van de server door middel van een GET-verzoek. Het verzoek
   bevat de naam van het bestand dat gedownload moet worden en andere parameters, zoals de
   locatie waar het bestand moet worden opgeslagen.
5. Bestand verwijderen
   De client stuurt een DELETE-verzoek om een bestand van de server te verwijderen. Het verzoek
   bevat de naam van het bestand dat moet worden verwijderd.
6. Sessie beëindigen
   Wanneer de client klaar is met synchroniseren, stuurt hij een verzoek om de sessie te beëindigen.


Wat moet er in de header 

- Authentication & authorization
Welke vormen van authenticatie en authorisatie worden ondersteund door de server?
welke vormen van authenticatie en authorisatie bestaan er ?
- Basic
- OAuth
- Client certificates
- Form-based authentication
- OpenID


- Content-length
Content-length is de lengte van de body in bytes.

- host: de hostnaam van de server
- from: de hostnaam van de client

Content types
- application/json
- multipart/form-data
- application/x-www-form-urlencoded
- text/plain
- text/html
- text/css
- text/javascript
- image/gif
- image/jpeg
- image/png
- image/svg+xml
- image/tiff

Gedeelde cache:

Een gedeelde cache is een cache die wordt gebruikt door meerdere gebruikers. 
Een voorbeeld hiervan is een proxy server.

Private cache
Een private cache is een cache die wordt gebruikt door een enkele gebruiker.

Last-Modified
De Last-Modified header geeft aan wanneer een bestand voor het laatst is aangepast.

Transfer-Encoding
De Transfer-Encoding header geeft aan hoe de body van een HTTP response is gecodeerd.
- compress
- gzip
- chunked
