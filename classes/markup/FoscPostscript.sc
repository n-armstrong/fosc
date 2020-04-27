/* ------------------------------------------------------------------------------------------------------------
• FoscPostscript

- postscript commands: http://www.math.ubc.ca/~cass/courses/ps.html

x = FoscPostscript(
	['charpath', "This is text.", true],
    ['closepath'],
    ['curveto', 0, 1, 1.5, 2, 3, 6],
    ['fill'],
    ['findfont', "Times Roman"],
    ['grestore'],
    ['gsave'],
    ['lineto', 3.0, -4.0],
    ['moveto', 3.0, -4.0],
    ['newpath'],
    ['rcurveto', 0, 1, 1.5, 2, 3, 6],
    ['rlineto', 1.2, 2.0],
    ['rmoveto', 1.2, 2.0],
    ['rotate', 45],
    ['scale', 2, 1],
    ['scalefont', 12],
    ['setdash', [2, 1], 3],
    ['setfont'],
    ['setgray', 0.75],
    ['setlinewidth', 12],
    ['setrgbcolor', 0.75, 1, 0.2],
    ['show', "This is text."],
    ['stroke'],
    ['translate', 10, 200]
);

x.format;


x = FoscPostscript(
    ['newpath'],
    ['moveto', 0, 0],
    ['lineto', 10, -10],
    ['stroke']
);

y = FoscPostscript(
    ['newpath'],
    ['setlinewidth', 2],
    ['moveto', 20, -10],
    ['lineto', 10, -10],
    ['stroke']
);

(x ++ y).write;
------------------------------------------------------------------------------------------------------------ */ 
FoscPostscript {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <commands, <operators;
    *new { |... commands|
    	^super.new.init(commands);
    }
    init { |argCommands|
    	var cmd, args;
    	operators = [];
    	commands = argCommands ?? { [] };
    	commands.do { |each|
    		# cmd, args = [each[0], each[1..]];
    		if (this.respondsTo(cmd).not) { ^error(cmd.asString + "is not a valid Postscript command.") };
    		this.performList(cmd, args);
    	};
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • ++ (abjad: __add__)
    
	a = FoscPostscript(['lineto', 10, 100]);
	b = FoscPostscript(['lineto', 100, 10]);
	(a ++ b).format;
    -------------------------------------------------------------------------------------------------------- */
    ++ { |postscript|
    	var newCommands;
    	if (postscript.isKindOf(FoscPostscript).not) {
    		^error("FoscPostscript::++: argument must be a FoscPostscript.");
    	};
    	newCommands = commands ++ postscript.commands;
    	^this.species.new(*newCommands);
    }
    /* --------------------------------------------------------------------------------------------------------
    • illustrate
    def __illustrate__(self):
    -------------------------------------------------------------------------------------------------------- */
    illustrate {
        var markup;
        markup = FoscMarkup.postscript(this);
        ^markup.illustrate;
    }
    /* --------------------------------------------------------------------------------------------------------
    • format

    a = FoscPostscriptOperator('moveto', 1.0, 1.0);
    b = FoscPostscriptOperator('lineto', 1.0, 1.0);
    c = FoscPostscriptOperator('findfont', "Times Roman");
    p = FoscPostscript(a, b, c);
    p.format;

    p = FoscPostscript();
    p.moveto(1.0, 1.0);
    p.lineto(1.0, 1.0);
    p.findfont("Times Roman");
    p.format;
    -------------------------------------------------------------------------------------------------------- */
    format {
    	^this.str;
    }
    /* --------------------------------------------------------------------------------------------------------
    • str

    Gets string representation of Postscript.
    
    Return string.
    
    a = FoscPostscriptOperator('moveto', 1.0, 1.0);
    b = FoscPostscriptOperator('lineto', 1.0, 1.0);
    c = FoscPostscriptOperator('findfont', "Times Roman");
    p = FoscPostscript(a, b, c);
    p.str;
    -------------------------------------------------------------------------------------------------------- */
    str {
        var result;
        if (operators.isEmpty) { ^"" };
        result = "";
        operators.do { |each| result = result ++ "\n%".format(each.format) };
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • write
    !!! should be named show -- see naming conflict with postcript show methods below
    !!! TRANSITIONAL - TO BE REMOVED

    x = FoscPostscript(
        ['lineto', 3.0, -4.0],
        ['moveto', 3.0, -4.0],
        ['newpath'],
        ['rcurveto', 0, 1, 1.5, 2, 3, 6],
        ['rlineto', 1.2, 2.0],
        ['rmoveto', 1.2, 2.0],
        ['rotate', 45],
        ['scale', 2, 1],
        ['scalefont', 12],
        ['setdash', [2, 1], 3],
        ['setfont'],
        ['setgray', 0.75],
        ['setlinewidth', 12],
        ['setrgbcolor', 0.75, 1, 0.2],
        ['show', "This is text."],
        ['stroke']
    );
    x.format;
    x.write("/Users/newton/Desktop/test.ps")
    -------------------------------------------------------------------------------------------------------- */
    // write {
    //     var lilypondFile;
    //     lilypondFile = LP_File();
    //     lilypondFile.append(this.illustrate);
    //     lilypondFile.write;
    // }
    write { |path|
        var file;
        file = File(path, "w");
        file.write(this.format);
        file.close;
        ^file;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    •
    ### PRIVATE METHODS ###
    
        @staticmethod
        def _format_argument(argument):
            if isinstance(argument, str):
                if argument.startswith('/'):
                    return argument
                return '({})'.format(argument)
            elif isinstance(argument, collections.Sequence):
                if not argument:
                    return '[ ]'
                contents = ' '.join(
                    Postscript._format_argument(_) for _ in argument
                    )
                return '[ {} ]'.format(contents)
            elif isinstance(argument, bool):
                return str(argument).lower()
            elif isinstance(argument, (int, float)):
                argument = mathtools.integer_equivalent_number_to_integer(argument)
                return str(argument)
            return str(argument)
    -------------------------------------------------------------------------------------------------------- */
    *prFormatArgument { |argument|
    	var contents;
    	case
    	{ argument.isKindOf(String) } {
    		if (argument[0] == $/) { ^argument };
    		^"(%)".format(argument);
    	}
    	{ argument.isKindOf(SequenceableCollection) } {
    		if (argument.isEmpty) { ^"[ ]" };
    		contents = "";
    		argument.do { |each| contents = contents + FoscPostscript.prFormatArgument(each) };
    		^"[ % ]".format(contents[1..]);
    	}
    	{ argument.isKindOf(Boolean) } {
    		^argument.asString;
    	}
    	{ argument.isKindOf(Number) } {
    		//!!! argument = mathtools.integer_equivalent_number_to_integer(argument)
    		^argument.asString;
    	};
    }
    /* --------------------------------------------------------------------------------------------------------
    • prWithOperator

    def _with_operator(self, operator):
            operators = self.operators or ()
            operators = operators + (operator,)
            return type(self)(operators)
    -------------------------------------------------------------------------------------------------------- */
    prWithOperator { |operator|
    	operators = operators.add(operator);
  		//^this.species.new(operators); //!!! is this needed for type checking ??
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    •
    ### PUBLIC METHODS ###
    
        def as_markup(self):
            r'''Converts postscript to markup.
    
            
• Example ---
    
                ::
    
                    >>> postscript = markuptools.Postscript()
                    >>> postscript = postscript.newpath()
                    >>> postscript = postscript.moveto(100, 200)
                    >>> postscript = postscript.lineto(200, 250)
                    >>> postscript = postscript.lineto(100, 300)
                    >>> postscript = postscript.closepath()
                    >>> postscript = postscript.gsave()
                    >>> postscript = postscript.setgray(0.5)
                    >>> postscript = postscript.fill()
                    >>> postscript = postscript.grestore()
                    >>> postscript = postscript.setlinewidth(4)
                    >>> postscript = postscript.setgray(0.75)
                    >>> postscript = postscript.stroke()
    
                ::
    
                    >>> markup = postscript.as_markup()
                    >>> print(format(markup))
                    \markup {
                        \postscript
                            #"
                            newpath
                            100 200 moveto
                            200 250 lineto
                            100 300 lineto
                            closepath
                            gsave
                            0.5 setgray
                            fill
                            grestore
                            4 setlinewidth
                            0.75 setgray
                            stroke
                            "
                        }
    
            Returns new markup.
            '''
            from abjad.tools import markuptools
            return markuptools.Markup.postscript(self)
    -------------------------------------------------------------------------------------------------------- */
    asMarkup {
    	^FoscMarkup.postscript(this);
    }
    /* --------------------------------------------------------------------------------------------------------
    • arc (not in abjad)

    p = FoscPostscript();
    p.moveto(30, -20);
    p.arc(20, -20, 10, 0, 360);
    p.stroke;
    p.write;
    -------------------------------------------------------------------------------------------------------- */
    arc { |x, y, radius, startAngle, endAngle|
        ^this.prWithOperator(FoscPostscriptOperator('arc', x, y, radius, startAngle, endAngle));
    }
    /* --------------------------------------------------------------------------------------------------------
    • charpath
    def charpath(self, text, modify_font=True):
            r'''Postscript ``charpath`` operator.
    
            
• Example ---
    
                ::
    
                    >>> postscript = markuptools.Postscript()
                    >>> postscript = postscript.findfont('Times Roman')
                    >>> postscript = postscript.scalefont(32)
                    >>> postscript = postscript.setfont()
                    >>> postscript = postscript.translate(100, 200)
                    >>> postscript = postscript.rotate(45)
                    >>> postscript = postscript.scale(2, 1)
                    >>> postscript = postscript.newpath()
                    >>> postscript = postscript.moveto(0, 0)
                    >>> postscript = postscript.charpath('This is text.', True)
                    >>> postscript = postscript.setlinewidth(0.5)
                    >>> postscript = postscript.setgray(0.25)
                    >>> postscript = postscript.stroke()
                    >>> print(str(postscript))
                    /Times-Roman findfont
                    32 scalefont
                    setfont
                    100 200 translate
                    45 rotate
                    2 1 scale
                    newpath
                    0 0 moveto
                    (This is text.) true charpath
                    0.5 setlinewidth
                    0.25 setgray
                    stroke
    
            Returns new Postscript.
            '''
            from abjad.tools import markuptools
            text = str(text)
            modify_font = bool(modify_font)
            operator = markuptools.PostscriptOperator(
                'charpath',
                text,
                modify_font,
                )
            return self._with_operator(operator)
    -------------------------------------------------------------------------------------------------------- */
    charpath { |text, modifyFont=true|
    	text = text.asString;
    	^this.prWithOperator(FoscPostscriptOperator('charpath', text, modifyFont));
    }
    /* --------------------------------------------------------------------------------------------------------
    •
    def closepath(self):
            r'''Postscript ``closepath`` operator.
    
            
• Example ---
    
                ::
    
                    >>> postscript = markuptools.Postscript()
                    >>> postscript = postscript.newpath()
                    >>> postscript = postscript.moveto(100, 200)
                    >>> postscript = postscript.lineto(200, 250)
                    >>> postscript = postscript.lineto(100, 300)
                    >>> postscript = postscript.closepath()
                    >>> postscript = postscript.gsave()
                    >>> postscript = postscript.setgray(0.5)
                    >>> postscript = postscript.fill()
                    >>> postscript = postscript.grestore()
                    >>> postscript = postscript.setlinewidth(4)
                    >>> postscript = postscript.setgray(0.75)
                    >>> postscript = postscript.stroke()
                    >>> print(str(postscript))
                    newpath
                    100 200 moveto
                    200 250 lineto
                    100 300 lineto
                    closepath
                    gsave
                    0.5 setgray
                    fill
                    grestore
                    4 setlinewidth
                    0.75 setgray
                    stroke
    
            Returns new Postscript.
            '''
            from abjad.tools import markuptools
            operator = markuptools.PostscriptOperator('closepath')
            return self._with_operator(operator)
    
	p = FoscPostscript();
    p.closepath;
    p.format;
    -------------------------------------------------------------------------------------------------------- */
    closepath {
    	^this.prWithOperator(FoscPostscriptOperator('closepath'));
    }
    /* --------------------------------------------------------------------------------------------------------
    •
    def curveto(self, x1, y1, x2, y2, x3, y3):
            r'''Postscript ``curveto`` operator.
    
            
• Example ---
    
                ::
    
                    >>> postscript = markuptools.Postscript()
                    >>> postscript = postscript.curveto(0, 1, 1.5, 2, 3, 6)
                    >>> print(str(postscript))
                    0 1 1.5 2 3 6 curveto
    
            Returns new Postscript.
            '''
            from abjad.tools import markuptools
            x1 = float(x1)
            x2 = float(x2)
            x3 = float(x3)
            y1 = float(y1)
            y2 = float(y2)
            y3 = float(y3)
            operator = markuptools.PostscriptOperator(
                'curveto',
                x1, y1,
                x2, y2,
                x3, y3,
                )
            return self._with_operator(operator)
    
	p = FoscPostscript();
    p.curveto(10, -10, 20, -30, 30, 0);
    p.stroke;
    p.write;
    -------------------------------------------------------------------------------------------------------- */
    curveto { |x1, y1, x2, y2, x3, y3|
    	//!!!TODO: do args need to be converted to floats ?
    	^this.prWithOperator(FoscPostscriptOperator('curveto', x1, y1, x2, y2, x3, y3));
    }
    /* --------------------------------------------------------------------------------------------------------
    •
    def fill(self):
            r'''Postscript ``fill`` operator.
    
            
• Example ---
    
                ::
    
                    >>> postscript = markuptools.Postscript()
                    >>> postscript = postscript.newpath()
                    >>> postscript = postscript.moveto(100, 200)
                    >>> postscript = postscript.lineto(200, 250)
                    >>> postscript = postscript.lineto(100, 300)
                    >>> postscript = postscript.closepath()
                    >>> postscript = postscript.gsave()
                    >>> postscript = postscript.setgray(0.5)
                    >>> postscript = postscript.fill()
                    >>> postscript = postscript.grestore()
                    >>> postscript = postscript.setlinewidth(4)
                    >>> postscript = postscript.setgray(0.75)
                    >>> postscript = postscript.stroke()
                    >>> print(str(postscript))
                    newpath
                    100 200 moveto
                    200 250 lineto
                    100 300 lineto
                    closepath
                    gsave
                    0.5 setgray
                    fill
                    grestore
                    4 setlinewidth
                    0.75 setgray
                    stroke
    
            Returns new Postscript.
            '''
            from abjad.tools import markuptools
            operator = markuptools.PostscriptOperator('fill')
            return self._with_operator(operator)

    p = FoscPostscript();
    p.fill;
    p.format;        
    -------------------------------------------------------------------------------------------------------- */
    fill {
    	^this.prWithOperator(FoscPostscriptOperator('fill'));
    }
    /* --------------------------------------------------------------------------------------------------------
    •
    def findfont(self, font_name):
            r'''Postscript ``findfont`` operator.
    
            
• Example ---
    
                ::
    
                    >>> postscript = markuptools.Postscript()
                    >>> postscript = postscript.findfont('Times Roman')
                    >>> postscript = postscript.scalefont(12)
                    >>> postscript = postscript.setfont()
                    >>> postscript = postscript.newpath()
                    >>> postscript = postscript.moveto(100, 200)
                    >>> postscript = postscript.show('This is text.')
                    >>> print(str(postscript))
                    /Times-Roman findfont
                    12 scalefont
                    setfont
                    newpath
                    100 200 moveto
                    (This is text.) show
    
            Returns new Postscript.
            '''
            from abjad.tools import markuptools
            font_name = str(font_name)
            font_name = font_name.replace(' ', '-')
            font_name = '/{}'.format(font_name)
            operator = markuptools.PostscriptOperator('findfont', font_name)
            return self._with_operator(operator)

    p = FoscPostscript();
    p.findfont('Times Roman');
    p.format;
    -------------------------------------------------------------------------------------------------------- */
    findfont { |fontName|
    	fontName = fontName.asString;
    	fontName = fontName.replace(" ", "-");
    	fontName = "/%".format(fontName);
    	^this.prWithOperator(FoscPostscriptOperator('findfont', fontName));
    }
    /* --------------------------------------------------------------------------------------------------------
    •
    def grestore(self):
            r'''Postscript ``grestore`` operator.
    
            
• Example ---
    
                ::
    
                    >>> postscript = markuptools.Postscript()
                    >>> postscript = postscript.newpath()
                    >>> postscript = postscript.moveto(100, 200)
                    >>> postscript = postscript.lineto(200, 250)
                    >>> postscript = postscript.lineto(100, 300)
                    >>> postscript = postscript.closepath()
                    >>> postscript = postscript.gsave()
                    >>> postscript = postscript.setgray(0.5)
                    >>> postscript = postscript.fill()
                    >>> postscript = postscript.grestore()
                    >>> postscript = postscript.setlinewidth(4)
                    >>> postscript = postscript.setgray(0.75)
                    >>> postscript = postscript.stroke()
                    >>> print(str(postscript))
                    newpath
                    100 200 moveto
                    200 250 lineto
                    100 300 lineto
                    closepath
                    gsave
                    0.5 setgray
                    fill
                    grestore
                    4 setlinewidth
                    0.75 setgray
                    stroke
    
            Returns new Postscript.
            '''
            from abjad.tools import markuptools
            operator = markuptools.PostscriptOperator('grestore')
            return self._with_operator(operator)
    
	p = FoscPostscript();
    p.grestore;
    p.format;
    -------------------------------------------------------------------------------------------------------- */
    grestore {
    	^this.prWithOperator(FoscPostscriptOperator('grestore'));
    }
    /* --------------------------------------------------------------------------------------------------------
    •
    def gsave(self):
            r'''Postscript ``gsave`` operator.
    
            
• Example ---
    
                ::
    
                    >>> postscript = markuptools.Postscript()
                    >>> postscript = postscript.newpath()
                    >>> postscript = postscript.moveto(100, 200)
                    >>> postscript = postscript.lineto(200, 250)
                    >>> postscript = postscript.lineto(100, 300)
                    >>> postscript = postscript.closepath()
                    >>> postscript = postscript.gsave()
                    >>> postscript = postscript.setgray(0.5)
                    >>> postscript = postscript.fill()
                    >>> postscript = postscript.grestore()
                    >>> postscript = postscript.setlinewidth(4)
                    >>> postscript = postscript.setgray(0.75)
                    >>> postscript = postscript.stroke()
                    >>> print(str(postscript))
                    newpath
                    100 200 moveto
                    200 250 lineto
                    100 300 lineto
                    closepath
                    gsave
                    0.5 setgray
                    fill
                    grestore
                    4 setlinewidth
                    0.75 setgray
                    stroke
    
            Returns new Postscript.
            '''
            from abjad.tools import markuptools
            operator = markuptools.PostscriptOperator('gsave')
            return self._with_operator(operator)
    
	p = FoscPostscript();
    p.gsave;
    p.format;
    -------------------------------------------------------------------------------------------------------- */
    gsave {
    	^this.prWithOperator(FoscPostscriptOperator('gsave'));
    }
    /* --------------------------------------------------------------------------------------------------------
    •
    def lineto(self, x, y):
            r'''Postscript ``lineto`` operator.
    
            
• Example ---
    
                ::
    
                    >>> postscript = markuptools.Postscript()
                    >>> postscript = postscript.moveto(1, 1)
                    >>> postscript = postscript.lineto(3, -4)
                    >>> postscript = postscript.stroke()
                    >>> print(str(postscript))
                    1 1 moveto
                    3 -4 lineto
                    stroke
    
            Returns new Postscript.
            '''
            from abjad.tools import markuptools
            x = float(x)
            y = float(y)
            operator = markuptools.PostscriptOperator('lineto', x, y)
            return self._with_operator(operator)
    
	p = FoscPostscript();
    p.lineto(3.0, -4.0);
    p.format;
    -------------------------------------------------------------------------------------------------------- */
    lineto { |x, y|
    	//!!!TODO: convert x and y to float ?
    	^this.prWithOperator(FoscPostscriptOperator('lineto', x, y));
    }
    /* --------------------------------------------------------------------------------------------------------
    •
    def moveto(self, x, y):
            r'''Postscript ``moveto`` operator.
    
            
• Example ---
    
                ::
    
                    >>> postscript = markuptools.Postscript()
                    >>> postscript = postscript.moveto(1, 1)
                    >>> postscript = postscript.lineto(3, -4)
                    >>> postscript = postscript.stroke()
                    >>> print(format(postscript))
                    markuptools.Postscript(
                        operators=(
                            markuptools.PostscriptOperator('moveto', 1.0, 1.0),
                            markuptools.PostscriptOperator('lineto', 3.0, -4.0),
                            markuptools.PostscriptOperator('stroke'),
                            ),
                        )
    
                ::
    
                    >>> print(str(postscript))
                    1 1 moveto
                    3 -4 lineto
                    stroke
    
            Returns new Postscript.
            '''
            from abjad.tools import markuptools
            x = float(x)
            y = float(y)
            operator = markuptools.PostscriptOperator('moveto', x, y)
            return self._with_operator(operator)
    
	p = FoscPostscript();
    p.moveto(3.0, -4.0);
    p.format;
    -------------------------------------------------------------------------------------------------------- */
    moveto { |x, y|
    	//!!!TODO: convert x and y to float ?
    	^this.prWithOperator(FoscPostscriptOperator('moveto', x, y));
    }
    /* --------------------------------------------------------------------------------------------------------
    •
    def newpath(self):
            r'''Postscript ``newpath`` operator.
    
            
• Example ---
    
                ::
    
                    >>> postscript = markuptools.Postscript()
                    >>> postscript = postscript.newpath()
                    >>> postscript = postscript.moveto(100, 200)
                    >>> postscript = postscript.lineto(200, 250)
                    >>> postscript = postscript.lineto(100, 300)
                    >>> postscript = postscript.closepath()
                    >>> postscript = postscript.gsave()
                    >>> postscript = postscript.setgray(0.5)
                    >>> postscript = postscript.fill()
                    >>> postscript = postscript.grestore()
                    >>> postscript = postscript.setlinewidth(4)
                    >>> postscript = postscript.setgray(0.75)
                    >>> postscript = postscript.stroke()
                    >>> print(str(postscript))
                    newpath
                    100 200 moveto
                    200 250 lineto
                    100 300 lineto
                    closepath
                    gsave
                    0.5 setgray
                    fill
                    grestore
                    4 setlinewidth
                    0.75 setgray
                    stroke
    
            Returns new Postscript.
            '''
            from abjad.tools import markuptools
            operator = markuptools.PostscriptOperator('newpath')
            return self._with_operator(operator)
    
	p = FoscPostscript();
    p.newpath;
    p.format;
    -------------------------------------------------------------------------------------------------------- */
    newpath {
    	^this.prWithOperator(FoscPostscriptOperator('newpath'));
    }
    /* --------------------------------------------------------------------------------------------------------
    •
    def rcurveto(self, dx1, dy1, dx2, dy2, dx3, dy3):
            r'''Postscript ``rcurveto`` operator.
    
            ..  container:: edxample
    
                ::
    
                    >>> postscript = markuptools.Postscript()
                    >>> postscript = postscript.rcurveto(0, 1, 1.5, 2, 3, 6)
                    >>> print(str(postscript))
                    0 1 1.5 2 3 6 rcurveto
    
            Returns new Postscript.
            '''
            from abjad.tools import markuptools
            dx1 = float(dx1)
            dx2 = float(dx2)
            dx3 = float(dx3)
            dy1 = float(dy1)
            dy2 = float(dy2)
            dy3 = float(dy3)
            operator = markuptools.PostscriptOperator(
                'rcurveto',
                dx1, dy1,
                dx2, dy2,
                dx3, dy3,
                )
            return self._with_operator(operator)
    
	p = FoscPostscript();
    p.rcurveto(0, 1, 1.5, 2, 3, 6);
    p.format;
    -------------------------------------------------------------------------------------------------------- */
    rcurveto { |dx1, dy1, dx2, dy2, dx3, dy3|
    	//!!!TODO: convert dx1, dy1, dx2, dy2, dx3, dy3 to float ?
    	^this.prWithOperator(FoscPostscriptOperator('rcurveto', dx1, dy1, dx2, dy2, dx3, dy3));
    }
    /* --------------------------------------------------------------------------------------------------------
    •
    def rlineto(self, dx, dy):
            r'''Postscript ``rlineto`` operator.
    
            
• Example ---
    
                ::
    
                    >>> postscript = markuptools.Postscript()
                    >>> postscript = postscript.rmoveto(1, 1)
                    >>> postscript = postscript.rlineto(3, -4)
                    >>> postscript = postscript.stroke()
                    >>> print(format(postscript))
                    markuptools.Postscript(
                        operators=(
                            markuptools.PostscriptOperator('rmoveto', 1.0, 1.0),
                            markuptools.PostscriptOperator('rlineto', 3.0, -4.0),
                            markuptools.PostscriptOperator('stroke'),
                            ),
                        )
    
                ::
    
                    >>> print(str(postscript))
                    1 1 rmoveto
                    3 -4 rlineto
                    stroke
    
            Returns new Postscript.
            '''
            from abjad.tools import markuptools
            dx = float(dx)
            dy = float(dy)
            operator = markuptools.PostscriptOperator('rlineto', dx, dy)
            return self._with_operator(operator)
    
	p = FoscPostscript();
    p.rlineto(1.2, 2.0);
    p.format;
    -------------------------------------------------------------------------------------------------------- */
    rlineto { |dx, dy|
    	//!!!TODO: convert dx, dy to float ?
    	^this.prWithOperator(FoscPostscriptOperator('rlineto', dx, dy));
    }
    /* --------------------------------------------------------------------------------------------------------
    •
    def rmoveto(self, dx, dy):
            r'''Postscript ``rmoveto`` operator.
    
            
• Example ---
    
                ::
    
                    >>> postscript = markuptools.Postscript()
                    >>> postscript = postscript.rmoveto(1, 1)
                    >>> postscript = postscript.rlineto(3, -4)
                    >>> postscript = postscript.stroke()
                    >>> print(format(postscript))
                    markuptools.Postscript(
                        operators=(
                            markuptools.PostscriptOperator('rmoveto', 1.0, 1.0),
                            markuptools.PostscriptOperator('rlineto', 3.0, -4.0),
                            markuptools.PostscriptOperator('stroke'),
                            ),
                        )
    
                ::
    
                    >>> print(str(postscript))
                    1 1 rmoveto
                    3 -4 rlineto
                    stroke
    
            Returns new Postscript.
            '''
            from abjad.tools import markuptools
            dx = float(dx)
            dy = float(dy)
            operator = markuptools.PostscriptOperator('rmoveto', dx, dy)
            return self._with_operator(operator)
    
	p = FoscPostscript();
    p.rmoveto(1.2, 2.0);
    p.format;
    -------------------------------------------------------------------------------------------------------- */
    rmoveto { |dx, dy|
    	//!!!TODO: convert dx, dy to float ?
    	^this.prWithOperator(FoscPostscriptOperator('rmoveto', dx, dy));
    }
    /* --------------------------------------------------------------------------------------------------------
    •
    def rotate(self, degrees):
            r'''Postscript ``restore`` operator.
    
            
• Example ---
    
                ::
    
                    >>> postscript = markuptools.Postscript()
                    >>> postscript = postscript.findfont('Times Roman')
                    >>> postscript = postscript.scalefont(32)
                    >>> postscript = postscript.setfont()
                    >>> postscript = postscript.translate(100, 200)
                    >>> postscript = postscript.rotate(45)
                    >>> postscript = postscript.scale(2, 1)
                    >>> postscript = postscript.newpath()
                    >>> postscript = postscript.moveto(0, 0)
                    >>> postscript = postscript.charpath('This is text.', True)
                    >>> postscript = postscript.setlinewidth(0.5)
                    >>> postscript = postscript.setgray(0.25)
                    >>> postscript = postscript.stroke()
                    >>> print(str(postscript))
                    /Times-Roman findfont
                    32 scalefont
                    setfont
                    100 200 translate
                    45 rotate
                    2 1 scale
                    newpath
                    0 0 moveto
                    (This is text.) true charpath
                    0.5 setlinewidth
                    0.25 setgray
                    stroke
    
            Returns new Postscript.
            '''
            from abjad.tools import markuptools
            degrees = float(degrees)
            operator = markuptools.PostscriptOperator('rotate', degrees)
            return self._with_operator(operator)
    
	p = FoscPostscript();
    p.rotate(45);
    p.format;
    -------------------------------------------------------------------------------------------------------- */
    rotate { |degrees|
    	//!!!TODO: convert degrees to float ?
    	//!!!TODO: SC custom version using radians rather than degrees (for consistency)
    	^this.prWithOperator(FoscPostscriptOperator('rotate', degrees));
    }
    /* --------------------------------------------------------------------------------------------------------
    •
    def scale(self, dx, dy):
            r'''Postscript ``scale`` operator.
    
            
• Example ---
    
                ::
    
                    >>> postscript = markuptools.Postscript()
                    >>> postscript = postscript.findfont('Times Roman')
                    >>> postscript = postscript.scalefont(32)
                    >>> postscript = postscript.setfont()
                    >>> postscript = postscript.translate(100, 200)
                    >>> postscript = postscript.rotate(45)
                    >>> postscript = postscript.scale(2, 1)
                    >>> postscript = postscript.newpath()
                    >>> postscript = postscript.moveto(0, 0)
                    >>> postscript = postscript.charpath('This is text.', True)
                    >>> postscript = postscript.setlinewidth(0.5)
                    >>> postscript = postscript.setgray(0.25)
                    >>> postscript = postscript.stroke()
                    >>> print(str(postscript))
                    /Times-Roman findfont
                    32 scalefont
                    setfont
                    100 200 translate
                    45 rotate
                    2 1 scale
                    newpath
                    0 0 moveto
                    (This is text.) true charpath
                    0.5 setlinewidth
                    0.25 setgray
                    stroke
    
            Returns new Postscript.
            '''
            from abjad.tools import markuptools
            dx = float(dx)
            dy = float(dy)
            operator = markuptools.PostscriptOperator('scale', dx, dy)
            return self._with_operator(operator)
    
	p = FoscPostscript();
    p.scale(2, 1);
    p.format;
    -------------------------------------------------------------------------------------------------------- */
    scale { |dx, dy|
    	//!!!TODO: convert dx, dy to float ?
    	^this.prWithOperator(FoscPostscriptOperator('scale', dx, dy));
    }
    /* --------------------------------------------------------------------------------------------------------
    •
    def scalefont(self, font_size):
            r'''Postscript ``scalefont`` operator.
    
            
• Example ---
    
                ::
    
                    >>> postscript = markuptools.Postscript()
                    >>> postscript = postscript.findfont('Times Roman')
                    >>> postscript = postscript.scalefont(12)
                    >>> postscript = postscript.setfont()
                    >>> postscript = postscript.newpath()
                    >>> postscript = postscript.moveto(100, 200)
                    >>> postscript = postscript.show('This is text.')
                    >>> print(str(postscript))
                    /Times-Roman findfont
                    12 scalefont
                    setfont
                    newpath
                    100 200 moveto
                    (This is text.) show
    
            Returns new Postscript.
            '''
            from abjad.tools import markuptools
            font_size = float(font_size)
            operator = markuptools.PostscriptOperator('scalefont', font_size)
            return self._with_operator(operator)
    
	p = FoscPostscript();
    p.scalefont(12);
    p.format;
    -------------------------------------------------------------------------------------------------------- */
    scalefont { |fontSize|
    	//!!!TODO: convert fontSize to float ?
    	^this.prWithOperator(FoscPostscriptOperator('scalefont', fontSize));
    }
    /* --------------------------------------------------------------------------------------------------------
    •
    def setdash(self, array=None, offset=0):
            r'''Postscript ``setdash`` operator.
    
            
• Example ---
    
                ::
    
                    >>> postscript = markuptools.Postscript().setdash([2, 1], 3)
                    >>> print(format(postscript))
                    markuptools.Postscript(
                        operators=(
                            markuptools.PostscriptOperator('setdash', (2.0, 1.0), 3.0),
                            ),
                        )
    
                ::
    
                    >>> print(str(postscript))
                    [ 2 1 ] 3 setdash
    
            
• Example ---
    
                ::
    
                    >>> postscript = markuptools.Postscript().setdash()
                    >>> print(format(postscript))
                    markuptools.Postscript(
                        operators=(
                            markuptools.PostscriptOperator('setdash', (), 0.0),
                            ),
                        )
    
                ::
    
                    >>> print(str(postscript))
                    [ ] 0 setdash
    
            Returns new Postscript.
            '''
            from abjad.tools import markuptools
            if array is None:
                array = ()
            else:
                array = tuple(float(_) for _ in array)
            offset = float(offset)
            operator = markuptools.PostscriptOperator('setdash', array, offset)
            return self._with_operator(operator)

    p = FoscPostscript();
    p.setdash([2, 1], 3);
    p.format;
    -------------------------------------------------------------------------------------------------------- */
    setdash { |array, offset|
    	array = array ?? { [] };
    	//!!!TODO: convert array, offset to float ?
    	^this.prWithOperator(FoscPostscriptOperator('setdash', array, offset));
    }
    /* --------------------------------------------------------------------------------------------------------
    •
    def setfont(self):
            r'''Postscript ``setfont`` operator.
    
            
• Example ---
    
                ::
    
                    >>> postscript = markuptools.Postscript()
                    >>> postscript = postscript.findfont('Times Roman')
                    >>> postscript = postscript.scalefont(12)
                    >>> postscript = postscript.setfont()
                    >>> postscript = postscript.newpath()
                    >>> postscript = postscript.moveto(100, 200)
                    >>> postscript = postscript.show('This is text.')
                    >>> print(str(postscript))
                    /Times-Roman findfont
                    12 scalefont
                    setfont
                    newpath
                    100 200 moveto
                    (This is text.) show
    
            Returns new Postscript.
            '''
            from abjad.tools import markuptools
            operator = markuptools.PostscriptOperator('setfont')
            return self._with_operator(operator)
    
	p = FoscPostscript();
    p.setfont;
    p.format;
    -------------------------------------------------------------------------------------------------------- */
    setfont {
    	^this.prWithOperator(FoscPostscriptOperator('setfont'));
    }
    /* --------------------------------------------------------------------------------------------------------
    •
    def setgray(self, gray_value):
            r'''Postscript ``setgray`` operator.
    
            
• Example ---
    
                ::
    
                    >>> postscript = markuptools.Postscript()
                    >>> postscript = postscript.newpath()
                    >>> postscript = postscript.moveto(100, 200)
                    >>> postscript = postscript.lineto(200, 250)
                    >>> postscript = postscript.lineto(100, 300)
                    >>> postscript = postscript.closepath()
                    >>> postscript = postscript.gsave()
                    >>> postscript = postscript.setgray(0.5)
                    >>> postscript = postscript.fill()
                    >>> postscript = postscript.grestore()
                    >>> postscript = postscript.setlinewidth(4)
                    >>> postscript = postscript.setgray(0.75)
                    >>> postscript = postscript.stroke()
                    >>> print(str(postscript))
                    newpath
                    100 200 moveto
                    200 250 lineto
                    100 300 lineto
                    closepath
                    gsave
                    0.5 setgray
                    fill
                    grestore
                    4 setlinewidth
                    0.75 setgray
                    stroke
    
            Returns new Postscript.
            '''
            from abjad.tools import markuptools
            gray_value = float(gray_value)
            assert 0 <= gray_value <= 1
            operator = markuptools.PostscriptOperator('setgray', gray_value)
            return self._with_operator(operator)

    p = FoscPostscript();
    p.setgray(0.75);
    p.format;
    -------------------------------------------------------------------------------------------------------- */
    setgray { |grayValue|
    	//!! gray_value = float(gray_value)
        //!!! assert 0 <= gray_value <= 1
    	^this.prWithOperator(FoscPostscriptOperator('setgray', grayValue));
    }
    /* --------------------------------------------------------------------------------------------------------
    •
    def setlinewidth(self, width):
            r'''Postscript ``setlinewidth`` operator.
    
            
• Example ---
    
                ::
    
                    >>> postscript = markuptools.Postscript()
                    >>> postscript = postscript.moveto(1, 1)
                    >>> postscript = postscript.setlinewidth(2.5)
                    >>> postscript = postscript.lineto(3, -4)
                    >>> postscript = postscript.stroke()
                    >>> print(format(postscript))
                    markuptools.Postscript(
                        operators=(
                            markuptools.PostscriptOperator('moveto', 1.0, 1.0),
                            markuptools.PostscriptOperator('setlinewidth', 2.5),
                            markuptools.PostscriptOperator('lineto', 3.0, -4.0),
                            markuptools.PostscriptOperator('stroke'),
                            ),
                        )
    
                ::
    
                    >>> print(str(postscript))
                    1 1 moveto
                    2.5 setlinewidth
                    3 -4 lineto
                    stroke
    
            Returns new Postscript.
            '''
            from abjad.tools import markuptools
            width = float(width)
            operator = markuptools.PostscriptOperator('setlinewidth', width)
            return self._with_operator(operator)

    p = FoscPostscript();
    p.setlinewidth(12.2);
    p.format;
    -------------------------------------------------------------------------------------------------------- */
    setlinewidth { |width|
    	//!! width = float(width)
    	^this.prWithOperator(FoscPostscriptOperator('setlinewidth', width));
    }
    /* --------------------------------------------------------------------------------------------------------
    •
    def setrgbcolor(self, red, green, blue):
            r'''Postscript ``setrgb`` operator.
    
            
• Example ---
    
                ::
    
                    >>> postscript = markuptools.Postscript()
                    >>> postscript = postscript.newpath()
                    >>> postscript = postscript.moveto(100, 100)
                    >>> postscript = postscript.rlineto(0, 100)
                    >>> postscript = postscript.rlineto(100, 0)
                    >>> postscript = postscript.rlineto(0, -100)
                    >>> postscript = postscript.rlineto(-100, 0)
                    >>> postscript = postscript.closepath()
                    >>> postscript = postscript.gsave()
                    >>> postscript = postscript.setrgbcolor(0.5, 1, 0.5)
                    >>> postscript = postscript.fill()
                    >>> postscript = postscript.grestore()
                    >>> postscript = postscript.setrgbcolor(1, 0, 0)
                    >>> postscript = postscript.setlinewidth(4)
                    >>> postscript = postscript.stroke()
    
                ::
    
                    >>> print(str(postscript))
                    newpath
                    100 100 moveto
                    0 100 rlineto
                    100 0 rlineto
                    0 -100 rlineto
                    -100 0 rlineto
                    closepath
                    gsave
                    0.5 1 0.5 setrgbcolor
                    fill
                    grestore
                    1 0 0 setrgbcolor
                    4 setlinewidth
                    stroke
    
            Returns new Postscript.
            '''
            from abjad.tools import markuptools
            red = float(red)
            green = float(green)
            blue = float(blue)
            assert 0 <= red <= 1
            assert 0 <= green <= 1
            assert 0 <= blue <= 1
            operator = markuptools.PostscriptOperator(
                'setrgbcolor',
                red,
                green,
                blue,
                )
            return self._with_operator(operator)

    p = FoscPostscript();
    p.setrgbcolor(0.75, 1, 0.2);
    p.format;
    -------------------------------------------------------------------------------------------------------- */
    setrgbcolor { |red, green, blue|
    	//red = float(red)
        //green = float(green)
        //blue = float(blue)
        //assert 0 <= red <= 1
        //assert 0 <= green <= 1
        //assert 0 <= blue <= 1
    	^this.prWithOperator(FoscPostscriptOperator('setrgbcolor', red, green, blue));
    }
    /* --------------------------------------------------------------------------------------------------------
    • show
    !!! TODO: solve naming conflict with FoscObject::show

    def show(self, text):
            r'''Postscript ``show`` operator.
    
            
• Example ---
    
                ::
    
                    >>> postscript = markuptools.Postscript()
                    >>> postscript = postscript.findfont('Times Roman')
                    >>> postscript = postscript.scalefont(12)
                    >>> postscript = postscript.setfont()
                    >>> postscript = postscript.newpath()
                    >>> postscript = postscript.moveto(100, 200)
                    >>> postscript = postscript.show('This is text.')
                    >>> print(str(postscript))
                    /Times-Roman findfont
                    12 scalefont
                    setfont
                    newpath
                    100 200 moveto
                    (This is text.) show
    
            Returns new Postscript.
            '''
            from abjad.tools import markuptools
            text = str(text)
            operator = markuptools.PostscriptOperator('show', text)
            return self._with_operator(operator)

    p = FoscPostscript();
    p.show("This is text.");
    p.format;
    -------------------------------------------------------------------------------------------------------- */
    show { |text|
    	text = text.asString;
    	^this.prWithOperator(FoscPostscriptOperator('show', text));
    }
    /* --------------------------------------------------------------------------------------------------------
    •
    def stroke(self):
            r'''Postscript ``stroke`` operator.
    
            
• Example ---
    
                ::
    
                    >>> postscript = markuptools.Postscript()
                    >>> postscript = postscript.lineto(3, -4)
                    >>> postscript = postscript.stroke()
                    >>> print(format(postscript))
                    markuptools.Postscript(
                        operators=(
                            markuptools.PostscriptOperator('lineto', 3.0, -4.0),
                            markuptools.PostscriptOperator('stroke'),
                            ),
                        )
    
                ::
    
                    >>> print(str(postscript))
                    3 -4 lineto
                    stroke
    
            Returns new Postscript.
            '''
            from abjad.tools import markuptools
            operator = markuptools.PostscriptOperator('stroke')
            return self._with_operator(operator)
    
	p = FoscPostscript();
    p.stroke;
    p.format;
    -------------------------------------------------------------------------------------------------------- */
    stroke {
    	^this.prWithOperator(FoscPostscriptOperator('stroke'));
    }
    /* --------------------------------------------------------------------------------------------------------
    •
    def translate(self, dx, dy):
            r'''Postscript ``translate`` operator.
    
            
• Example ---
    
                ::
    
                    >>> postscript = markuptools.Postscript()
                    >>> postscript = postscript.findfont('Times Roman')
                    >>> postscript = postscript.scalefont(32)
                    >>> postscript = postscript.setfont()
                    >>> postscript = postscript.translate(100, 200)
                    >>> postscript = postscript.rotate(45)
                    >>> postscript = postscript.scale(2, 1)
                    >>> postscript = postscript.newpath()
                    >>> postscript = postscript.moveto(0, 0)
                    >>> postscript = postscript.charpath('This is text.', True)
                    >>> postscript = postscript.setlinewidth(0.5)
                    >>> postscript = postscript.setgray(0.25)
                    >>> postscript = postscript.stroke()
                    >>> print(str(postscript))
                    /Times-Roman findfont
                    32 scalefont
                    setfont
                    100 200 translate
                    45 rotate
                    2 1 scale
                    newpath
                    0 0 moveto
                    (This is text.) true charpath
                    0.5 setlinewidth
                    0.25 setgray
                    stroke
    
            Returns new Postscript.
            '''
            from abjad.tools import markuptools
            dx = float(dx)
            dy = float(dy)
            operator = markuptools.PostscriptOperator('translate', dx, dy)
            return self._with_operator(operator)
    
	p = FoscPostscript();
    p.translate(100, 300);
    p.format;
    -------------------------------------------------------------------------------------------------------- */
    translate { |dx, dy|
    	//!!! convert dx, dy to float ??
    	^this.prWithOperator(FoscPostscriptOperator('translate', dx, dy));
    }
}
