/* ------------------------------------------------------------------------------------------------------------
• FoscMarkupCommand

m = FoscMarkupCommand('draw-circle', 0, 1, 2, 3);
FoscMarkup(m).format;
------------------------------------------------------------------------------------------------------------ */
FoscMarkupCommand : Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <command, <args, <forceQuotes=false;
    *new { |command ... args|
        ^super.new.init(command, args);
    }
    init { |argCommand, argArgs|
        command = argCommand;
        args = argArgs;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS: SPECIAL
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • ==

    Is true when `argument` is a markup command with command and arguments equal to those of this markup command. Otherwise false.
    
    Returns true or false.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • format

    Formats markup command.

    Returns string.
    -------------------------------------------------------------------------------------------------------- */
    format {
        ^this.prGetLilypondFormat;
    }
    /* --------------------------------------------------------------------------------------------------------
    • hash

    Hashes markup command.

    Returns integer.
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    • asCompileString (abjad: __repr__)

    Gets markup command interpreter representation.

    Returns string.
    -------------------------------------------------------------------------------------------------------- */
    asCompileString {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • str

    Gets string representation of markup command.

    Returns string.
    -------------------------------------------------------------------------------------------------------- */
    str {
        ^this.prGetLilypondFormat;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prEscapeString
    -------------------------------------------------------------------------------------------------------- */
    prEscapeString { |string|
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetFormatPieces
    
    m = FoscMarkupCommand('draw-circle', 0, 1, 2, 3);
    FoscMarkup(m).format;

    m = FoscMarkupCommand('column', "LosAngeles", "May - August 2014");
    FoscMarkup(m).format;

    m = FoscMarkupCommand('column', ["Los Angeles", "May - August 2014"]);
    FoscMarkup(m).format;

    m = FoscMarkupCommand('column', ["Los Angeles", "May - August 2014"]);
    FoscMarkup(m).format;
    -------------------------------------------------------------------------------------------------------- */
    prGetFormatPieces {
        var recurse, result, formatted, indent, parts;
        recurse = { |iterable|
            result = [];
            iterable.do { |each|
                case
                { each.isSequenceableCollection && { each.isString.not } } {
                    result = result.add("{");
                    result = result.addAll(recurse.(each));
                    result = result.add("}");
                }
                { each.isKindOf(FoscScheme) } {                    result = result.add(each.format);
                }
                { each.respondsTo('prGetFormatPieces') } {
                    result = result.addAll(each.prGetFormatPieces);
                }
                { each.isString && { each.includesAny("\n") } } {
                    result = result.add("#\"");
                    result = result.addAll(each.splitLines);
                    result = result.add("\"");
                }
                {
                    formatted = FoscScheme.formatSchemeValue(each, forceQuotes);
                    if (each.isString) {
                        result = result.add(formatted);
                    } {
                        result = result.add("#%".format(formatted));
                    };
                };
            };
            result.collect { |each| "%%".format(indent, each) };
        };
        indent = FoscLilyPondFormatManager.indent;
        parts = ["\\" ++ "%".format(command)];
        parts = parts.addAll(recurse.(args));
        ^parts;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetLilypondFormat

    m = FoscMarkupCommand('draw-circle', 0, 1, 2, 3);
    m.prGetLilypondFormat;
    -------------------------------------------------------------------------------------------------------- */
    prGetLilypondFormat {
        ^this.prGetFormatPieces.join("\n");
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • argument

    Gets markup command arguments.

    Returns array.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • command

    Gets markup command name.

    Returns string.

    # TODO: change to MarkupCommand.name
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • forceQuotes

    Is true when markup command should force quotes around arguments. Otherwise false.

    The rendered result of forced and unforced quotes is the same.

    Defaults to false.

    Returns true or false.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • forceQuotes_
    -------------------------------------------------------------------------------------------------------- */
    forceQuotes_ { |bool|
        forceQuotes = bool.asBoolean;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *combineMarkupCommands

    Combines markup command and / or strings.

    LilyPond's '\combine' markup command can only take two arguments, so in order to combine more than two stencils, a cascade of '\combine' commands must be employed.  `combine_markup_commands` simplifies this process.
        
    Returns a markup command instance, or a string if that was the only argument.

    @staticmethod
    def combine_markup_commands(*commands):
        from abjad.tools import markuptools

        assert len(commands)
        assert all(
            isinstance(command, (markuptools.MarkupCommand, str))
            for command in commands
            )

        if 1 == len(commands):
            return commands[0]

        combined = MarkupCommand('combine', commands[0], commands[1])
        for command in commands[2:]:
            combined = MarkupCommand('combine', combined, command)
        return combined
    -------------------------------------------------------------------------------------------------------- */
    *combineMarkupCommands {
        ^this.notYetImplemented(thisMethod);
    }
}
