# Labo échecs

# Table des matières

- [Labo échecs](#labo-échecs)
    - [Endpoints](#endpoints)
        - [Tournois](#tournois)
            - [Get](#get)
                - [GetOne](#getone)
                - [Get tableau de score](#get-tableau-de-score)
                - [Top10](#top10)
            - [Post](#post)
                - [Create tournoi](#create-tournoi)
                - [Search](#search)
                - [Set résultat rencontre](#set-résultat-rencontre)
            - [Patch](#patch)
                - [Tour suivant](#tour-suivant)
                - [Désinscription](#désinscription)
                - [Démarrer tournoi](#démarrer-tournoi)
            - [Put](#put)
                - [Inscription](#inscription)
            - [Delete](#delete)
                - [Supprimer tournoi](#supprimer-tournoi)
        - [Joueurs](#joueurs)
            - [Post](#post-1)
                - [login](#login)
                - [inscription (joueur)](#inscription-joueur)
    - [Données](#données)
        - [MrCheckmate](#mrcheckmate)

## Endpoints

### Tournois

#### Get

##### GetOne

###### URL

- /api/tournoi/{id}

###### Paramètres

```java
Long id;
```

###### Réponse

```json
{
  "id": 0,
  "nom": "string",
  "lieu": "string",
  "nombreJoueursInscrits": 0,
  "nombreMinJoueurs": 0,
  "nombreMaxJoueurs": 0,
  "eLOMin": 0,
  "eLOMax": 0,
  "categories": [
    "JUNIOR"
  ],
  "statut": "EN_ATTENTE_DE_JOUEURS",
  "womenOnly": true,
  "dateFinInscriptions": "2024-02-26",
  "rondeCourante": 0,
  "recontresActuelles": [
    {
      "id": 0,
      "tournoiId": 0,
      "pseudoJoueurBlanc": "string",
      "pseudoJoueurNoir": "string",
      "ronde": 0,
      "resultat": "PAS_ENCORE_JOUEE"
    }
  ]
}
```
##### Get tableau de score

###### URL

- /api/{tournoiId}/tableauScores/{ronde}

###### Paramètres

```java
Long tournoiId;
Int ronde;
```

###### Réponse

```json
[
  {
    "nom": "string",
    "rencontreJouees": 0,
    "victoires": 0,
    "defaites": 0,
    "egalite": 0,
    "score": 0
  }
]
```

##### Top10

###### URL

- /api/tournoi/top10

###### Paramètres

    /

###### Réponse

```json
[
    {
        "id": 0,
        "nom": "string",
        "lieu": "string",
        "nombreJoueursInscrits": 0,
        "nombreMinJoueurs": 0,
        "nombreMaxJoueurs": 0,
        "eLOMin": 0,
        "eLOMax": 0,
        "categories": [
            "JUNIOR"
        ],
        "statut": "EN_ATTENTE_DE_JOUEURS",
        "womenOnly": true,
        "dateFinInscriptions": "2024-02-26",
        "rondeCourante": 0,
        "joueursInscrits": [
            {
                "pseudo": "string",
                "email": "string",
                "dateDeNaissance": "2024-02-26",
                "eLO": 0,
                "genre": "GARCON"
            }
        ]
    }
]
```

#### Post

##### Create tournoi

###### URL

- /api/tournoi/creation

###### Body

```json
{
  "nom": "string",
  "lieu": "string",
  "nombreMinJoueurs": 32,
  "nombreMaxJoueurs": 32,
  "eLOMin": 3000,
  "eLOMax": 3000,
  "categories": [
    "JUNIOR"
  ],
  "statut": "EN_ATTENTE_DE_JOUEURS",
  "womenOnly": true,
  "dateFinInscriptions": "2024-02-26"
}
```

##### Search

###### URL

- /api/tournoi/recherche
- 
###### Paramètres

```json
{
  "page": 0,
  "size": 1,
  "sort": [
    "string"
  ]
}
```

###### Body

```json
{
    "nom": "string",
    "statut": "EN_ATTENTE_DE_JOUEURS",
    "categories": [
      "JUNIOR"
    ]
}
```

###### Réponse

```json
{
  "totalPages": 0,
  "totalElements": 0,
  "size": 0,
  "content": [
    {
      "id": 0,
      "nom": "string",
      "lieu": "string",
      "nombreJoueursInscrits": 0,
      "nombreMinJoueurs": 0,
      "nombreMaxJoueurs": 0,
      "eLOMin": 0,
      "eLOMax": 0,
      "categories": [
        "JUNIOR"
      ],
      "statut": "EN_ATTENTE_DE_JOUEURS",
      "womenOnly": true,
      "dateFinInscriptions": "2024-02-26",
      "rondeCourante": 0,
      "joueursInscrits": [
        {
          "pseudo": "string",
          "email": "string",
          "dateDeNaissance": "2024-02-26",
          "eLO": 0,
          "genre": "GARCON"
        }
      ]
    }
  ],
  "number": 0,
  "sort": {
    "empty": true,
    "sorted": true,
    "unsorted": true
  },
  "pageable": {
    "offset": 0,
    "sort": {
      "empty": true,
      "sorted": true,
      "unsorted": true
    },
    "pageNumber": 0,
    "pageSize": 0,
    "paged": true,
    "unpaged": true
  },
  "numberOfElements": 0,
  "first": true,
  "last": true,
  "empty": true
}
```

##### Set résultat rencontre

###### URL

- /api/tournoi/rencontre/{rencontreId}/resultat

###### Paramètres

```java
    Long rencontreId;
```

###### Body

    PAS_ENCORE_JOUEE
    BLANC
    NOIR
    EGALITE

#### Patch

##### Tour suivant

###### URL

- /api/tournoi/tourSuivant/{tournoiId}

###### Paramètre

```java
    Long tournoiId;
```

##### Désinscription

###### URL

- /api/tournoi/desinscription/{tournoiId}

###### Paramètre

```java
    Long tournoiId;
```

##### Démarrer tournoi

###### URL

- /api/tournoi/demarrer/{id}

###### Paramètres

```java
    Long tournoiId;
```

#### Put

##### Inscription

###### URL

- /api/tournoi/inscription/{tournoiId}

###### Paramètre

```java
    Long tournoiId;
```

#### Delete

##### Supprimer tournoi

###### URL

- /api/tournoi/{id}

###### Paramètre

```java
    Long tournoiId;
```

### Joueurs

#### Post

##### login

###### URL

- /api/joueur/login

###### Body

```json
{
  "identifiant": "string",
  "motDePasse": "string"
}
```

###### Réponse

```json
{
  "token": "string",
  "role": "ADMIN",
  "username": "string"
}
```

##### inscription (joueur)

###### URL

- /api/joueur/inscription

###### Body

```json
{
  "pseudo": "string",
  "email": "string",
  "dateDeNaissance": "2024-02-26",
  "genre": "GARCON",
  "eLO": 3000,
  "role": "ADMIN"
}
```

## Données

### MrCheckmate

```json
{
  "pseudo" : "Mr Checkmate",
  "email" : "checkmate@chess.be",
  "motDePasse": "Test1234="
}
```