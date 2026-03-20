# Finna API -kuvaselain

Natiivi Android-sovellus, joka hakee ja esittää historiallisia maisemakuvia Finnan avoimesta REST API -rajapinnasta. Toteutettu MVVM-arkkitehtuurilla ja Jetpack Composella.

## Teknologiat ja arkkitehtuuri
* **Käyttöliittymä (View):** Jetpack Compose (`LazyVerticalStaggeredGrid`, `HorizontalPager`)
* **Tilanhallinta (ViewModel):** MVVM, Kotlin Coroutines, `StateFlow`
* **Verkkokerros (Model):** Retrofit2 & Gson
* **Kuvien lataus:** Coil (`AsyncImage`)

## Keskeiset ominaisuudet
* **Dynaaminen API-haku:** Vuosiluvun valinta (1880–2020) Compose Sliderilla.
* **Loputon kuvavirta (Infinite Scroll):** Lataa automaattisesti lisää tuloksia listan lopussa (`page`, `limit`).
* **Koko ruudun selaus:** Kuvan suurentaminen ja tulosten selaus pyyhkäisemällä.
* **Kuvien satunnaistaminen:** Haku hyödyntää dynaamista siemenlukua (`random_seed`), jotta tulokset vaihtuvat joka haulla.

## API-integraatio
Sovellus tekee HTTP GET -pyyntöjä Finnan rajapintaan asynkronisesti.
* **Base URL:** `https://api.finna.fi/`
* **Esimerkkipyyntö (vapaasanahaku + kuvarajaus):**
  `https://api.finna.fi/api/v1/search?lookfor=maisema+1920&filter[]=format:"0/Image/"&limit=20`

## Suorittaminen
1. Avaa projekti Android Studiossa.
2. Synkronoi Gradle-riippuvuudet (Retrofit, Coil, Gson).
3. Aja sovellus internet-yhteydellä varustetussa emulaattorissa tai fyysisessä laitteessa.