"use strict";

/** Referenz auf RichtText-Editor "quilljs". */
let quillEditor = null;

/**
 * Wenn eine Artikel-ID als URL-Parameter übergeben wurde, dann ist dieser Wert != -1
 * und die Seite ist im Änderungsmodus (andernfalls im Erstellungsmodus).
 */
let artikelID = -1;


/**
 * Hole die Artikel-ID aus der URL (URL-Parameter "artikelID").
 *
 * @return {number} Die Artikel-ID; -1 wenn keine ID gefunden wurde.
 */
function holeArtikelID() {

    const urlParams = new URLSearchParams(window.location.search);
    const artikelIdString = urlParams.get( "artikelID" );

    if ( artikelIdString === null ) {

        console.log( "URL-Parameter \"artikelID\" nicht gefunden." );
        return -1;
    }

    const artikelId = parseInt( artikelIdString, 10 );
    if ( isNaN( artikelId ) ) {

        console.error( "Wert von URL-Parameter \"artikelId\" ist keine Zahl: " + artikelIdString );
        return -1;
    }

    return artikelId;
}


/**
 * Initialisiert den QuillJS-Editor, sobald die Seite geladen wurde.
 */
document.addEventListener("DOMContentLoaded", function() {

    quillEditor = new Quill( "#quilljs_editor", {
        modules: {
            toolbar: [ // leerer Array: Standard-Optionen
                ["bold", "italic", "underline", "strike"],
                [{ "header": 1 }, { "header": 2 }],
                [{ "list": "ordered"}, { "list": "bullet" }],
                [{ "script": "sub"}, { "script": "super" }],
                [{ "color": [] }],
                [{ "align": [] }],
                ["clean"]
            ]
        },
        theme: "snow",
        placeholder: "Hier tollen Blog-Artikel verfassen..."
    });
    console.log( "QuillJS-Editor initialisiert." );

    artikelID = holeArtikelID();
    if ( artikelID === -1 ) {

        console.log( "Keine Artikel-ID in der URL gefunden: Erstellungsmodus." );

    } else {

        console.log( `Artikel-ID ${artikelID} in der URL gefunden: Änderungsmodus.` );
        artikelLaden();
    }

});


/**
 * Für den Änderungsmodus: Artikel von REST-Endpunkt laden und in den Editor darstellen.
 */
function artikelLaden() {

    const url = "/api/v1/holen/" + artikelID;

    console.log( "Versuche Artikel vom Server zu laden: " + url );

    fetch( url, { method: "GET" })
    .then(response => {

        if ( !response.ok ) {

            throw new Error( "Artikel zum Bearbeiten konnte nicht von Server abgerufen werden." );
        }

        // im Erfolgsfall enthält der Response-Body ein JSON-Objekt mit den Daten des Artikels
        return response.json();
    })
    .then( json => {

        document.getElementById( "titel").value = json.titel;

        const deltaObjekt = JSON.parse( json.deltaInhalt );
        quillEditor.setContents( deltaObjekt );
    })
    .catch( error => {

        alert( error.message );
    });
}


/**
 * Event-Handler für den Button "Speichern".
 */
function speichern() {

    console.log( "Auf Speichern gedrückt." );

    let titel = document.getElementById( "titel").value;
    titel = titel.trim();
    if (titel === "") {

        alert("Titel darf nicht leer sein.");
        return;
    }

    const deltaObjekt = quillEditor.getContents();
    const deltaString = JSON.stringify( deltaObjekt );
    const htmlContent = quillEditor.root.innerHTML;
    const plainString = quillEditor.getText();

    const payloadObjekt = {
                            artikelID  : artikelID  , // -1 für neuen Artikel
                            titel      : titel      ,
                            inhaltDelta: deltaString,
                            inhaltHTML : htmlContent,
                            inhaltPlain: plainString
                          };

    const payloadString = JSON.stringify( payloadObjekt );

    const url = artikelID == -1 ? "/api/v1/neu" : "/api/v1/aendern";

    fetch( url, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: payloadString
    })
    .then(response => {

        if ( !response.ok ) {

            throw new Error( "Fehler beim Speichern des Artikels." );
        }

        // im Erfolgsfall enthält der Response-Body nur den Pfad des neuen/geänderten Artikels
        return response.text();
    })
    .then( text => {

        window.location.href = text;
    })
    .catch( error => {

        alert( error.message );
    });
}

