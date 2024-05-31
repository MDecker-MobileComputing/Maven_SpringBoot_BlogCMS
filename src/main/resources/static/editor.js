"use strict";

let quillEditor = null;
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

        console.error( "URL-Parameter \"artikelID\" nicht gefunden." );
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
 * Initialize the QuillJS-Editor, sobald die Seite geladen wurde.
 */
document.addEventListener("DOMContentLoaded", function() {

    /*
    artikelID = holeArtikelID();
    if ( artikelID === -1 ) {

        alert( "FEHLER: Seite wurde ohne Artikel-ID aufgerufen." );
        return;
    }
    console.log( "Artikel-ID gefunden: " + artikelID );
    */

    quillEditor = new Quill( "#quilljs_editor", {
        modules: {
            table: true  // Aktiviert das Tabellenmodul
        },
        theme: "snow",
        placeholder: "Hier tollen Blog-Artikel schreiben..."
    });

  console.log( "QuillJS-Editor initialisiert." );
});


/**
 * Event-Handler für den Button "Speichern".
 */
function speichern() {

    console.log( "Auf Speichern gedrückt."  );

    let titel = document.getElementById( "titel").value;
    titel = titel.trim();
    if (titel === "") {

        alert("Titel darf nicht leer sein.");
        return;
    }

    const deltaObjekt = quillEditor.getContents();
    const deltaString = JSON.stringify( deltaObjekt );
    console.log( "Delta-String von quilljs: " + deltaString );

    const htmlContent = quillEditor.root.innerHTML;
    console.log( "HTML-String von quilljs: " + htmlContent );

    const payloadObjekt = {
        artikelID  : artikelID,
        titel      : titel,
        inhaltDelta: deltaString,
        inhaltHTML : htmlContent
    };

    const payloadString = JSON.stringify( payloadObjekt );

    fetch( "/api/v1/speichern", {
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

        // im Erfolgsfall enthält der Response-Body nur den Pfad des neuen Artikels
        return response.text();
    })
    .then( text => {

        alert( "Artikel wurde gespeichert." );
        window.location.href = text;
    })
    .catch( error => {

        alert( error.message );
    });
}

