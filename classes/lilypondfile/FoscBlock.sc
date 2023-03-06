/* ------------------------------------------------------------------------------------------------------------
• FoscBlock

!!!TODO: update, using 'instVarDict' instead of 'vars' variable ??

A LilyPond file block.

a = FoscBlock(name: 'paper');
a.leftMargin = FoscLilyPondDimension(2, 'cm');
a.rightMargin = FoscLilyPondDimension(2, 'cm');
a.format;


a = FoscBlock(name: 'header');
a.title_("Missa sexti tonus");
a.composer_("Josquin");
a.format;
------------------------------------------------------------------------------------------------------------ */
FoscBlock : Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <name, <escapedName, <items, <publicAttributeNames, <vars;
    *new { |name='score'|
        name = name.asSymbol;
        ^super.new.init(name);
    }
    init { |argName|
        name = argName;
        escapedName = "\\" ++ name.asString;
        items = List[];
        publicAttributeNames = List[];
        vars = ();
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • format

    Formats block.

    Returns string.
    -------------------------------------------------------------------------------------------------------- */
    format {
        ^this.prGetLilypondFormat;
    }
    /* --------------------------------------------------------------------------------------------------------
    • at (abjad: __getitem__)

    Gets item with name.

    Returns item or nil.

    a = FoscBlock('score');
    a.items.add(FoscScore(name: 'example_score'));
    a.format;
    a['example_score'];
    a['foo'];
    -------------------------------------------------------------------------------------------------------- */
    at { |name|
        items.do { |item|
            if (item.respondsTo('name') && { item.name == name }) { ^item };
            if (item.respondsTo('sourceLilypondType') && { item.sourceLilypondType == name }) { ^item };
        };
        //^throw("%::at: no item found with name: %.".format(this.species, name));
        ^nil;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prFormattedContextBlocks
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    prFormattedContextBlocks {
        var result, contextBlocks;
        
        result = [];
        contextBlocks = [];
        
        items.do { |item|
            if (item.isKindOf(FoscContextBlock)) {
                contextBlocks = contextBlocks.add(item);
            };
        };
        
        contextBlocks.do { |contextBlock|
            result = result.addAll(contextBlock.prGetFormatPieces);
        };
        
        ^result;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prFormatItem    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    prFormatItem { |item, depth=1|
        var indent, result, string, pieces;
        indent = FoscLilyPondFormatManager.indent.ditto(depth);
        result = [];
        case
        { item.isSequenceableCollection && { item.isString.not } } {
            result = result.add(indent ++ "{");
            item.do { |each| result = result.addAll(this.prFormatItem(each, depth + 1)) };
            result = result.add(indent ++ "}");
        }
        { item.isString } {
            string = indent ++ item;
            result = result.add(string);
        }
        { item.respondsTo('prGetFormatPieces') } {
            pieces = item.prGetFormatPieces;
            pieces = pieces.collect { |each| indent ++ each };
            result = result.addAll(pieces);
        };
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetFormatPieces

    a = FoscContext([FoscNote(60, 1/4)]);
    f = a.illustrate;
    f.format;
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0 !!!TODO: add tag stuff
    prGetFormatPieces { |tag|
        var indent, result, string, type, formattedAttributes, formattedContextBlocks;
        
        indent = FoscLilyPondFormatManager.indent;
        result = [];
        
        if (
            this.prGetFormattedUserAttributes.isEmpty
            && { (this.respondsTo('contexts') && { this.contexts.notNil }).not }
            && { (this.respondsTo('contextBlocks') && { this.contextBlocks.notNil }).not }
            && { items.size < 1 }
        ) {
            if (name == 'score') { ^"" };
            string = "% {}".format(escapedName);
            result = result.add(string);
            ^result;
        };
        
        string = "% {".format(escapedName);
        result = result.add(string);
        type = [FoscLeaf, FoscMarkup];
        
        items.do { |item|
            if (item.isKindOf(FoscContextBlock).not) {
                if (type.any { |type| item.isKindOf(type) }) { item = [item] };
                result = result.addAll(this.prFormatItem(item));
            };
        };
        
        formattedAttributes = this.prGetFormattedUserAttributes;
        formattedAttributes = formattedAttributes.collect { |each| indent ++ each };
        result = result.addAll(formattedAttributes);
        
        if (this.respondsTo('prFormattedContextBlocks') && { this.prFormattedContextBlocks.notNil} ) {
            formattedContextBlocks = this.prFormattedContextBlocks;
        } {
            formattedContextBlocks = [];
        
        };
        
        formattedContextBlocks = formattedContextBlocks.collect { |each| indent ++ each };
        result = result.addAll(formattedContextBlocks);
        result = result.add("}");

        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetFormattedUserAttributes
    -------------------------------------------------------------------------------------------------------- */
    prGetFormattedUserAttributes {
        var result, type, key, val, formattedKey, prGetFormattedValue, setting;
        result = [];
        items.do { |each|
            if (each.isKindOf(FoscScheme)) {
                result = result.add(each.format('lilypond'));
            };
        };
        type = [FoscLilyPondDimension, FoscScheme];
        //!!!TODO
        // for key in self._public_attribute_names:
        //     assert not key.startswith('_'), repr(key)
        //     value = getattr(self, key)
        //     # format subkeys via double underscore
        //     formatted_key = key.split('__')
        //     for i, k in enumerate(formatted_key):
        //         formatted_key[i] = k.replace('_', '-')
        //         if 0 < i:
        //             string = f"#'{formatted_key[i]}"
        //             formatted_key[i] = string
        //     formatted_key = ' '.join(formatted_key)
        vars.asSortedArray.do { |each|
            # key, val = each;
            formattedKey = key;
            case
            { val.isKindOf(FoscMarkup) } {
                prGetFormattedValue = val.prGetFormatPieces;
            }
            { type.any { |type| val.isKindOf(type) } } {
                prGetFormattedValue = [val.format('lilypond')];
            }
            {
                prGetFormattedValue = FoscScheme(val);
                prGetFormattedValue = [prGetFormattedValue.format('lilypond')];
            };
            setting = "% = %".format(formattedKey, prGetFormattedValue[0]);
            result = result.add(setting);
            result = result.addAll(prGetFormattedValue[1..]);
        };
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • doesNotUnderstand : test

    // WORKING   
    a = FoscBlock("score");
    a.title = FoscMarkup("Mass in C Minor");
    a.leftMargin = FoscLilyPondDimension(2, 'cm');
    a.maxSystemsPerPage = 1;
    a.raggedBottom = false;
    a.format;

    // BROKEN
    a = FoscBlock("score");
    a.accidentalStyle = FoscLilyPondLiteral("modern-cautionary");
    a.override.metronomeMark.xOffset = -2.2;
    override(a).proportionalNotationDuration_(FoscSchemeMoment(1, 28));
    a.format;

    abj: set_(score).proportional_notation_duration = schemetools.SchemeMoment(1, 16)

    a.items.add(FoscLilyPondLiteral("\\accidentalStyle modern-cautionary"));
    a.items.add(FoscLilyPondLiteral("\\override MetronomeMark.X-offset = -2.2"));
    a.items.add(FoscLilyPondLiteral("\\proportionalNotationDuration = #(ly:make-moment 1 28)")); //!!! NO

    // SHOULD PRODUCE:
    \accidentalStyle modern-cautionary // abj: a.items.append(LilyPondCommand('accidentalStyle modern-cautionary'))
    \override MetronomeMark.X-offset = -2.2
    proportionalNotationDuration = #(ly:make-moment 1 28)
    -------------------------------------------------------------------------------------------------------- */
    doesNotUnderstand { |selector, expr|
        var key;
        key = selector.asString.toLowerDashCase.asSymbol;
        if (expr.isString) { expr = FoscMarkup(expr) };
        vars[key] = expr;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetLilypondFormat
    -------------------------------------------------------------------------------------------------------- */
    prGetLilypondFormat {
        ^this.prGetFormatPieces.join("\n");
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • items

    Gets items in block.

    Returns list.

    a = FoscBlock(name: 'score');
    m = FoscMarkup('foo');
    a.items.add(m);
    a.items;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • name
    
    Gets name of block.

    Returns string.

    a = FoscBlock(name: 'score');
    a.name;
    -------------------------------------------------------------------------------------------------------- */
}
