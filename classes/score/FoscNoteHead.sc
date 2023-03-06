/* ------------------------------------------------------------------------------------------------------------
• FoscNoteHead

A note-head.


p = #[['size', 12]];
a = FoscNoteHead(writtenPitch: 61, tweaks: p);
a.tweak.color = 'red';
a.tweak.prGetAttributePairs;
a.format;
------------------------------------------------------------------------------------------------------------ */
FoscNoteHead : Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <alternative, <client, <isCautionary, <isForced, <isParenthesized, <tweaks, <writtenPitch;
    *new { |writtenPitch, client, isCautionary=false, isForced=false, isParenthesized=false, tweaks| 
        ^super.new.init(writtenPitch, client, isCautionary, isForced, isParenthesized, tweaks);
    }
    init { |writtenPitch, argClient, argIsCautionary, argIsForced, argIsParenthesized, argTweaks|
        var noteHead, key, val;
        
        case
        { writtenPitch.isKindOf(this.species) } {
            noteHead = writtenPitch;
            writtenPitch = noteHead.writtenPitch;
            argIsCautionary = noteHead.isCautionary;
            argIsForced = noteHead.isForced;
            tweaks = noteHead.tweaks;
        };
        
        this.writtenPitch_(writtenPitch ? 60);
        client = argClient;
        isCautionary = argIsCautionary;
        isForced = argIsForced;
        isParenthesized = argIsParenthesized;
        FoscLilyPondTweakManager.setTweaks(this, argTweaks);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
     /* --------------------------------------------------------------------------------------------------------
    • asCompileString

    Gets interpreter representation of note-head.
    
    Returns string.

    !!!TODO: incomplete

    a = FoscNoteHead(writtenPitch: 61, tweaks: p);
    a.cs;
    -------------------------------------------------------------------------------------------------------- */
    asCompileString {
        ^"FoscNoteHead('%')".format(writtenPitch.pitchName);
    }
    /* --------------------------------------------------------------------------------------------------------
    • copy

    Copies note-head.

    Returns new note-head.
    -------------------------------------------------------------------------------------------------------- */
    copy {
        ^this.species.new(writtenPitch, nil, isCautionary, isForced, isParenthesized, tweaks);
    }
    /* --------------------------------------------------------------------------------------------------------
    • ==

    Is true when argument is a note-head with written writtenPitch equal to that of this note-head. Otherwise false.

    Returns true or false.
    
    -------------------------------------------------------------------------------------------------------- */
    == { |object|
        if (object.isKindOf(this.species)) {
           ^(writtenPitch == object.writtenPitch);
        };

        ^false;
    }
    /* --------------------------------------------------------------------------------------------------------
    • format

    Formats note-head.
    
    Returns string.

    
    a = FoscNoteHead(writtenPitch: "Db4");
    a.format;

    a = FoscNoteHead(writtenPitch: FoscPitch("Db4"));
    a.format;

    a = FoscNoteHead(writtenPitch: FoscPitch("Db4"), isForced: true, isCautionary: true, isParenthesized: true);
    a.format;
    -------------------------------------------------------------------------------------------------------- */
    format {
        ^this.prGetLilypondFormat;
    }
    /* --------------------------------------------------------------------------------------------------------
    • hash

    Hashes note-head.

    Returns integer.

    !!!TODO: not yet implemented.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • <

    Is true when argument is a note-head with written writtenPitch greater than that of this note-head. Otherwise false.
    
    Returns true or false.

    !!!TODO: not yet implemented.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • str

    String representation of note-head.

    Returns string.

    a = FoscNoteHead(60, isCautionary: true);
    a.str;
    -------------------------------------------------------------------------------------------------------- */
    str {
        var result;
        result = "";
        
        if (this.writtenPitch.notNil) {
            result = this.writtenPitch.str;
            if (isForced) { result = result ++ "!" };
            if (isCautionary) { result = result ++ "?" };
        };
        
        ^result;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prGetFormatPieces

    a = FoscNoteHead(writtenPitch: 61, isCautionary: true);
    a.prGetFormatPieces;
    -------------------------------------------------------------------------------------------------------- */
    prGetFormatPieces {
        var result, strings, kernel;
        
        assert(writtenPitch.notNil);
        result = [];
        if (isParenthesized) { result = result.add("\\parenthesize") };
        
        if (tweaks.notNil) {
            strings = tweaks.prListFormatContributions(directed: false);
            result = result.addAll(strings);
        };  
        
        kernel = writtenPitch.format;
        if (isForced) { kernel = kernel ++ "!" };   
        if (isCautionary) { kernel = kernel ++ "?" };
        result = result.add(kernel);
        
        ^result; 
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetLilypondFormat
    
    a = FoscNoteHead("Db4");
    a.prGetLilypondFormat;

    a = FoscNoteHead("Db4", isForced: true, isCautionary: true, isParenthesized: true);
    a.prGetLilypondFormat;

    a = FoscNoteHead("Db4");
    a.tweak.color = 'red';
    a.prGetLilypondFormat;

    a = FoscNote(60, 1/4);
    tweak(a.noteHead).style = 'harmonic';
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    prGetLilypondFormat { |formattedDuration|
        var pieces, pieces_, result;
        pieces = this.prGetFormatPieces;

        if (formattedDuration.notNil) {
            pieces[pieces.lastIndex] = pieces[pieces.lastIndex] ++ formattedDuration;
        };
        
        if (alternative.notNil) {
            pieces = FoscLilyPondFormatManager.tag(pieces);
            pieces_ = alternative[0].prGetFormatPieces;
            if (formattedDuration.notNil) {
                pieces_[pieces_.lastIndex] = pieces_[pieces_.lastIndex] ++ formattedDuration;
            };
            pieces_ = FoscLilyPondFormatManager.tag(pieces_, deactivate: true, tag: alternative[1]);
            pieces = pieces.addAll(pieces_);
        };

        result = pieces.join("\n");
        
        ^result;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • alternative
    
    !!!TODO: not yet implemented
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • alternative_

    !!!TODO: not yet implemented
    -------------------------------------------------------------------------------------------------------- */
    alternative_ { |object|
        ^this.notYetImplemented(thisMethod);
        // if argument is not None:
        //     assert isinstance(argument, tuple), repr(argument)
        //     assert len(argument) == 3, repr(argument)
        //     assert isinstance(argument[0], NoteHead), repr(argument)
        //     assert argument[0].alternative is None, repr(argument)
        //     assert isinstance(argument[1], str), repr(argument)
        //     assert isinstance(argument[2], str), repr(argument)
        // self._alternative = argument
    }
    /* --------------------------------------------------------------------------------------------------------
    • client

    Client of note-head.
    
    Returns note, chord or nil.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • isCautionary

    Gets isCautionary accidental flag.

    Returns true or false.
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    • isCautionary_
    
    Sets isCautionary accidental flag.
    -------------------------------------------------------------------------------------------------------- */
    isCautionary_ { |bool|
        bool = bool.asBoolean;
        isCautionary = bool;
    }
    /* --------------------------------------------------------------------------------------------------------
    • isForced

    Gets isForced accidental flag.

    Returns true or false.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • isForced_
    
    Sets isForced accidental flag.
    -------------------------------------------------------------------------------------------------------- */
    isForced_ { |bool|
        bool = bool.asBoolean;
        isForced = bool;
    }
    /* --------------------------------------------------------------------------------------------------------
    • isParenthesized

    Gets isParenthesized flag.

    Returns true or false.
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    • isParenthesized_
    
    Sets isParenthesized flag.
    -------------------------------------------------------------------------------------------------------- */
    isParenthesized_ { |bool|
        bool = bool.asBoolean;
        isParenthesized = bool;
    }
    /* --------------------------------------------------------------------------------------------------------
    • tweaks

    Gets tweaks.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • writtenPitch

    Gets written pitch of note-head.

    Returns a FoscPitch.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • writtenPitch_
    
    Sets written pitch of note-head.

    a = FoscNoteHead(60);
    a.writtenPitch.cs;

    a.writtenPitch_("F#4");
    a.writtenPitch.cs;
    -------------------------------------------------------------------------------------------------------- */
    writtenPitch_ { |pitch|
        pitch = FoscPitch(pitch);
        writtenPitch = pitch;
        if (alternative.notNil) { alternative[0].writtenPitch_(pitch) };
    }
}
