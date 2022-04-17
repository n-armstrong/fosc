/* ------------------------------------------------------------------------------------------------------------
• Symbol
------------------------------------------------------------------------------------------------------------ */
+ Symbol {
	/* --------------------------------------------------------------------------------------------------------
	• toTridirectionalLilypondSymbol

	Changes receiver to tridirectional direction string.

	Returns string or nil.

	toTridirectionalDirectionString('^');
	toTridirectionalDirectionString('-');
	toTridirectionalDirectionString('_');
	toTridirectionalDirectionString('default');
    -------------------------------------------------------------------------------------------------------- */
    toTridirectionalLilypondString {
    	var lookup;
		lookup = (
			'above': 'up',
		    '^': 'up',
		    'up': 'up',
		    'below': 'down',
		    '_': 'down',
		    'down': 'down',
		    'center': 'center',
		    '-': 'center',
		    'center': 'center',
		    'default': 'center',
		    'neutral': 'center',
		    'nil': nil
		  );
		^lookup[this];
    }
    /* --------------------------------------------------------------------------------------------------------
	• toTridirectionalLilypondSymbol

	Changes receiver to tridirectional direction symbol.

	Returns string or nil.

	toTridirectionalDirectionString('^');
	toTridirectionalDirectionString('-');
	toTridirectionalDirectionString('_');
	toTridirectionalDirectionString('default');
    -------------------------------------------------------------------------------------------------------- */
    toTridirectionalLilypondSymbol {
    	var lookup;
		lookup = (
			'above': '^',
		    '^': '^',
		    'up': '^',
		    'below': '_',
		    '_': '_',
		    'down': '_',
		    'center': '-',
		    '-': '-',
		    'center': '-',
		    'default': '-',
		    'neutral': '-',
		    'nil': '-'
		  );
		^lookup[this];
    }
    /* --------------------------------------------------------------------------------------------------------
	• toTridirectionalOrdinalConstant

	Changes receiver to tridirectional ordinal constant.

	Returns string or nil.

	toTridirectionalOrdinalConstant('^');
	toTridirectionalOrdinalConstant('-');
	toTridirectionalOrdinalConstant('_');
	toTridirectionalOrdinalConstant('default');

	lookup = {
        Up: Up,
        '^': Up,
        'up': Up,
        1: Up,
        Down: Down,
        '_': Down,
        'down': Down,
        -1: Down,
        Center: Center,
        '-': Center,
        0: Center,
        'center': Center,
        'default': Center,
        'neutral': Center,
        }
    if argument is None:
        return None
    elif argument in lookup:
        return lookup[argument]
    message = 'unrecognized expression: {!r}.'
    message = message.format(argument)
    raise ValueError(message)
    -------------------------------------------------------------------------------------------------------- */
    toTridirectionalOrdinalConstant {
    	^this.notYetImplemented(thisMethod);
    }
}