/* ------------------------------------------------------------------------------------------------------------
• FoscLilyPondFile


A Lilypond file.


• Example 1

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
f = FoscLilyPondFile(a);
f.show;


• Example 2: Set proportional notation duration.

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
f = FoscLilyPondFile(a);
b = f.layoutBlock['Score'];
if (b.isNil) {
    b = FoscContextBlock(sourceLilypondType: 'Score');
    f.layoutBlock.items.add(b);
};
set(b).proportionalNotationDuration = FoscSchemeMoment([1, 20]);
f.show;
------------------------------------------------------------------------------------------------------------ */
FoscLilyPondFile : Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <comments, <dateTimeToken, <paperSize, <staffSize, <includes, <items;
    var <lilypondLanguageToken, <lilypondVersionToken, <useRelativeIncludes, <isStylesheet;
    var <headerBlock, <layoutBlock, <paperBlock, <scoreBlock; //!!!TODO: REMOVE ?
    *new { |items, dateTimeToken, paperSize, comments, includes, staffSize,
        lilypondLanguageToken, lilypondVersionToken, useRelativeIncludes=false, isStylesheet=false|     
        
        ^super.new.init(items, dateTimeToken, paperSize, comments, includes, staffSize,
            lilypondLanguageToken, lilypondVersionToken, useRelativeIncludes, isStylesheet);
    }
    init { |argItems, argDateTimeToken, argPaperSize, argComments, argIncludes, argStaffSize,
        argLilypondLanguageToken, argLilypondVersionToken, argUseRelativeIncludes, argIsStylesheet|
        var tuning;

        items = List[
            FoscBlock(name: 'header'),
            FoscBlock(name: 'layout'),
            FoscBlock(name: 'paper'),
            FoscBlock(name: 'score')
        ];
        
        # headerBlock, layoutBlock, paperBlock, scoreBlock = items;
        
        if (argItems.notNil) { scoreBlock.items.add(argItems) };
        // items = List[argItems] ?? { List[] };
        if (dateTimeToken.isNil) { dateTimeToken = FoscDateTimeToken() };
        comments = argComments ? [];
        if (comments.notEmpty) { comments = comments.collect { |each| each.asString } };
        paperSize = argPaperSize;
        includes = argIncludes ? [];
        
        if (includes.notEmpty) { includes = includes.collect { |each| each.asString } };
        staffSize = argStaffSize;
        lilypondLanguageToken = argLilypondLanguageToken;
        lilypondLanguageToken = lilypondLanguageToken ?? { FoscLilyPondLanguageToken() };
        lilypondLanguageToken = FoscLilyPondLanguageToken(lilypondLanguageToken);
        lilypondVersionToken = argLilypondVersionToken;
        lilypondVersionToken = lilypondVersionToken ?? { FoscLilyPondVersionToken() };
        lilypondVersionToken = FoscLilyPondVersionToken(lilypondVersionToken);
        useRelativeIncludes = argUseRelativeIncludes;
        isStylesheet = argIsStylesheet;

        // tuning = FoscPitchManager.tuning;
        
        // if (isStylesheet.not && { tuning.notNil }) {
        //     includes = includes.add("%/%.ily".format(Fosc.stylesheetDirectory, tuning.name));
        // };
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • comments
    
    Gets comments of Lilypond file.
    
    a = FoscLilyPondFile();
    a.comments;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • dateTimeToken
    
    Gets date-time token.
    
    a = FoscLilyPondFile();
    a.dateTimeToken;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • paperSize

    Gets default paper size of Lilypond file. Set to pair or nil. Defaults to nil.
    
    a = FoscLilyPondFile();
    a.paperSize;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • staffSize

    Gets global staff size of Lilypond file. Set to number or nil. Defaults to nil.
    
    a = FoscLilyPondFile();
    a.staffSize;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • headerBlock

    Gets header block.

    Returns block or nil.
    
    a = FoscLilyPondFile();
    a.headerBlock.name;
    -------------------------------------------------------------------------------------------------------- */
    // headerBlock {
    //     items.do { |item|
    //         if (item.isKindOf(FoscBlock) && { item.name == 'header' }) { ^item };
    //     };
    //     ^nil;
    // }
    /* --------------------------------------------------------------------------------------------------------
    • includes

    Gets includes of Lilypond file.
    
    a = FoscLilyPondFile();
    a.includes;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • items

    Gets items in Lilypond file.
    
    a = FoscLilyPondFile();
    a.items;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • layoutBlock

    Gets layout block.

    Returns block or nil.

    
    a = FoscLilyPondFile();
    a.layoutBlock.name;
    -------------------------------------------------------------------------------------------------------- */
    // layoutBlock {
    //     items.do { |item|
    //         if (item.isKindOf(FoscBlock) && { item.name == 'layout' }) { ^item };
    //     };
    //     ^nil;
    // }
    /* --------------------------------------------------------------------------------------------------------
    • lilypondLanguageToken

    Gets Lilypond language token.
    
    a = FoscLilyPondFile();
    a.lilypondLanguageToken;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • lilypondVersionToken

    Gets Lilypond version token.

    a = FoscLilyPondFile();
    a.lilypondVersionToken;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • paperBlock

    Gets paper block.

    Returns block or nil.

    
    a = FoscLilyPondFile();
    a.paperBlock.name;
    -------------------------------------------------------------------------------------------------------- */
    // paperBlock {
    //     items.do { |item|
    //         if (item.isKindOf(FoscBlock) && { item.name == 'paper' }) { ^item };
    //     };
    //     ^nil;
    // }
    /* --------------------------------------------------------------------------------------------------------
    • scoreBlock

    Gets score block.

    Returns block or nil.
    

    a = FoscLilyPondFile();
    a.scoreBlock.name;
    -------------------------------------------------------------------------------------------------------- */
    // scoreBlock {
    //     items.do { |item|
    //         if (item.isKindOf(FoscBlock) && { item.name == 'score' }) { ^item };
    //     };
    //     ^nil;
    // }
    /* --------------------------------------------------------------------------------------------------------
    • useRelativeIncludes

    Is true when Lilypond file should use relative includes.

    a = FoscLilyPondFile();
    a.useRelativeIncludes;
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • format

    Formats Lilypond file.

    Returns string.
    -------------------------------------------------------------------------------------------------------- */
    format {
        ^this.prGetLilypondFormat;
    }
    /* --------------------------------------------------------------------------------------------------------
    • at (abjad: __getitem__)
    
    Gets item with name.

    Returns item.

    Raises key error when no item with name is found.
    -------------------------------------------------------------------------------------------------------- */
    at { |name|
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • illustrate
    
    Illustrates Lilypond file.

    Returns Lilypond file unchanged.
    -------------------------------------------------------------------------------------------------------- */
    illustrate {
        ^this;
    }
    /* --------------------------------------------------------------------------------------------------------
    • asCompileString

    Gets interpreter representation of Lilypond file.
    
    Returns string.
    -------------------------------------------------------------------------------------------------------- */
    asCompileString {
        ^this.notYetImplemented(thisMethod);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prGetFormatPieces
    
    c = ["File construct as an example.", "Second comment."];
    i = ["external-settings-file-1.ly", "external-settings-file-2.ly"];
    a = FoscLilyPondFile(comments: c, includes: i, staffSize: 16);
    a.format;
    -------------------------------------------------------------------------------------------------------- */
    prGetFormatPieces { |tag|
        var result, includes, string;

        result = [];
        includes = [];

        result = result.addAll(this.prGetFormattedComments);
        
        if (lilypondVersionToken.notNil) {
            string = "%".format(lilypondVersionToken.format);
            includes = includes.add(string);
        };
        
        if (lilypondLanguageToken.notNil) {
            string = "%".format(lilypondLanguageToken.format);
            includes = includes.add(string);
        };
        
        includes = includes.join("\n");

        if (includes.notEmpty) {
            result = result.add(includes);
        };

        if (useRelativeIncludes) {
            string = "#(ly:set-option 'relative-includes #t)";
            result = result.add(string);
        };
        
        result = result.addAll(this.prGetFormattedIncludes);
        result = result.addAll(this.prGetFormattedSchemeSettings);
        result = result.addAll(this.prGetFormattedBlocks);
        
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetFormattedBlocks

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    f = FoscLilyPondFile(a);
    f.prGetFormattedBlocks;

    a = FoscContext([FoscNote(60, 1/4)]);
    f = a.illustrate;
    f.format;

    FoscBlock
    -------------------------------------------------------------------------------------------------------- */
    prGetFormattedBlocks {
        var result, string;
       
        result = [];
        
        items.do { |item|
            case 
            { item.isKindOf(FoscBlock) && { item.items.isEmpty } } {
                // pass
            }
            { item.respondsTo('prGetLilypondFormat') && { item.isString.not } } {
                
                try {
                    string = item.prGetLilypondFormat;
                } {
                    string = item.prGetLilypondFormat;
                };

                if (string.notNil) { result = result.add(string) };
            }
            {
                result = result.add(item.asString);
            };
        };

        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
   • prGetFormattedComments

    c = ["File construct as an example.", "A second comment on a new line."];
    a = FoscLilyPondFile(comments: c);
    a.prGetFormattedComments;
    -------------------------------------------------------------------------------------------------------- */
    prGetFormattedComments {
        var result, lilypondFormat;
        result = [];
        comments.do { |comment|
            if (comment.respondsTo('prGetLilypondFormat') && { comment.isString.not }) {
                lilypondFormat = comment.format;
                //"lilypondFormat".postln; lilypondFormat.postln;
                if (lilypondFormat.notEmpty) { result = result.add("\\% %".format(comment)) };
            } {
                result = result.add("\\% %".format(comment));
            };
        };
        if (result.notEmpty) { result = [result.join("\n")] };
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetFormattedIncludes
    
    i = [
        "external-settings-file-1.ly",
        "external-settings-file-2.ly"
    ];
    a = FoscLilyPondFile(includes: i);
    a.prGetFormattedIncludes;
    -------------------------------------------------------------------------------------------------------- */
    prGetFormattedIncludes {
        var result, string;
        result = [];
        includes.do { |include|
            if (includes.includes(include)) {
                if (include.isString) {
                    string = "\\include \"%\"".format(include);
                    result = result.add(string);
                } {
                    result = result.add(include.format);
                };
            };
        };
        if (result.notEmpty) { result = [result.join("\n")] };
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetFormattedSchemeSettings

    a = FoscLilyPondFile(paperSize: #['a5', 'portrait'], staffSize: 16);
    a.paperSize;
    a.prGetFormattedSchemeSettings;
    -------------------------------------------------------------------------------------------------------- */
    prGetFormattedSchemeSettings {
        var result, dimension, orientation, string;
        result = [];
        if (paperSize.notNil) {
            # dimension, orientation = paperSize;
            string = "#(set-default-paper-size \"%\" '%)";
            string = string.format(dimension, orientation);
            result = result.add(string);
        };
        if (staffSize.notNil) {
            string = "#(set-global-staff-size %)";
            string = string.format(staffSize);
            result = result.add(string);
        };
        if (result.notEmpty) { result = [result.join("\n")] };
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetLilypondFormat
    -------------------------------------------------------------------------------------------------------- */
    prGetLilypondFormat {
        ^this.prGetFormatPieces.join("\n\n");
    }
}
