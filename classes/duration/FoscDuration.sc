/* ------------------------------------------------------------------------------------------------------------
• FoscDuration

FoscDuration(1, 4).str;
FoscDuration(0.25).str;
FoscDuration(pi).str;
FoscDuration(inf).str;

FoscDuration(4, 4).str;
FoscDuration(3, 8).str;
FoscDuration(7, 16).str;
FoscDuration(17, 16).str;   // not assignable error
------------------------------------------------------------------------------------------------------------ */
FoscDuration : FoscFraction {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • abs
    
    Gets absolute value of duration.
    
    Returns nonnegative duration.
    
    a = FoscDuration(-1, 4);
    b = a.abs;
    b.str;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • asCompileString

    a = FoscDuration(1, 4);
    a.asCompileString;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • +
   
    Adds duration to expr.

    Returns duration.


    • Example 1
    

    Returns duration when expr is a duration.

    a = FoscDuration(1, 2);
    b = FoscDuration(3, 2);
    c = (a + b);
    c.str;


    • Example 2

    
    Returns nonreduced fraction when expr is a nonreduced fraction.

    a = FoscDuration(1, 2);
    b = FoscNonreducedFraction(3, 2);
    c = (a + b);
    c.str;
    -------------------------------------------------------------------------------------------------------- */
    + { |expr|
        if (expr.isKindOf(FoscNonreducedFraction)) {
            ^(expr + this);
        } {
            ^this.species.new(FoscFraction(this) + expr);
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • asFloat

    FoscDuration(3, 2).asFloat;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • /

    Divides duration by expr.
    
    Returns multiplier.


    Returns multiplier when expr is a duration.

    a = FoscDuration(2, 4);
    b = FoscDuration(2, 1);
    c = (a / b);
    c.str;

    
    Returns nonreduced fraction when expr is a nonreduced fraction.

    a = FoscDuration(2, 4);
    b = FoscNonreducedFraction(2, 1);
    c = (a / b);
    c.str;
    -------------------------------------------------------------------------------------------------------- */
    / { |expr|
        case
        { expr.isKindOf(this.species) } {
            ^FoscMultiplier(FoscFraction(this) / expr)
        }
        { expr.isKindOf(FoscNonreducedFraction) } {
            ^(FoscNonreducedFraction(this) / expr);
        }
        {
            ^this.species.new(FoscFraction(this) / expr);
        };     
    }
    /* --------------------------------------------------------------------------------------------------------
    • divmod
    
    Equals the pair (duration // args, duration % args).

    Returns pair.

    a = FoscDuration(7, 4);
    b = a.divmod(FoscDuration(4, 4));
    b.collect { |each| each.str };
    -------------------------------------------------------------------------------------------------------- */
    divmod { |expr|
        var div, mod;
        div = this.div(expr);
        mod = this.mod(expr);
        ^[div, mod];
    }
    /* --------------------------------------------------------------------------------------------------------
    • == 

    Is true when duration equals arg. Otherwise false.
    
    Returns true or false.

    FoscDuration(4, 4) == FoscDuration(4, 4);
    FoscDuration(4, 4) == FoscDuration(2, 4);
    FoscDuration(4, 4) == FoscDuration(2, 2);
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • format

    Formats duration.
    
    Set format_specification to '' or 'storage'. Interprets '' equal to 'storage'.
    
    Returns string.

    FoscDuration(1, 4).format;


    def __format__(self, format_specification=''):
        from abjad.tools import systemtools
        if format_specification in ('', 'storage'):
            return systemtools.StorageFormatAgent(self).get_storage_format()
        return str(self)
    -------------------------------------------------------------------------------------------------------- */
    format { |formatSpecification=''|
        ^this.str;
    }
    /* --------------------------------------------------------------------------------------------------------
    • >=
    
    Is true when duration is greater than or equal to arg. Otherwise false.
    
    Returns true or false.
    
    FoscDuration(4, 4) >= FoscDuration(4, 4);
    FoscDuration(4, 4) >= FoscDuration(2, 4);
    FoscDuration(4, 4) >= FoscDuration(2, 2);
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • >

    Is true when duration is greater than arg. Otherwise false.

    Returns true or false.

    FoscDuration(4, 4) > FoscDuration(4, 4);
    FoscDuration(4, 4) > FoscDuration(2, 4);
    FoscDuration(4, 4) > FoscDuration(2, 2);
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • <=

    Is true when duration is less than or equal to arg. Otherwise false.
    
    Returns true or false.
    
    FoscDuration(4, 4) <= FoscDuration(4, 4);
    FoscDuration(4, 4) <= FoscDuration(2, 4);
    FoscDuration(4, 4) <= FoscDuration(2, 2);
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • <

    Is true when duration is less than arg. Otherwise false.
    
    Returns true or false.
    
    FoscDuration(4, 4) < FoscDuration(4, 4);
    FoscDuration(4, 4) < FoscDuration(2, 4);
    FoscDuration(4, 4) < FoscDuration(2, 2);
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • mod

    Modulus operator applied to duration.
    
    Returns duration.
    
    a = FoscDuration(4, 4);
    b = FoscDuration(2, 4);
    
    c = (a % b);
    c.str;
    
    d = (b % a);
    d.str;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • *

    Duration multiplied by expr.
    
    Returns duration.
    

    Returns duration when expr is a duration.

    a = FoscDuration(1, 2);
    b = FoscDuration(3, 2);
    c = (a * b);
    c.str;

    
    Returns nonreduced fraction when expr is a nonreduced fraction.

    a = FoscDuration(1, 2);
    b = FoscNonreducedFraction(3, 6);
    c = (a * b);
    c.str;
    -------------------------------------------------------------------------------------------------------- */
    * { |expr|
        if (expr.isKindOf(FoscNonreducedFraction)) {
            ^(expr * this);
        } {
            ^this.species.new(FoscFraction(this) * expr);
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • !=
    
    Is true when duration does not equal arg. Otherwise false.
    
    Returns true or false.
    
    FoscDuration(4, 4) != FoscDuration(4, 4);
    FoscDuration(4, 4) != FoscDuration(2, 4);
    FoscDuration(4, 4) != FoscDuration(2, 2);
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • neg

    Negates duration.
    
    Returns new duration.

    a = FoscDuration(1, 4);
    b = a.neg;
    b.str;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • **
    
    Raises duration to expr power.
    
    Returns new duration.

    a = FoscDuration(4, 2) ** 2;
    a.str;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • reduce
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    • -

    Subtracts expr from duration.
    
    Returns new duration.

    Returns duration when expr is a duration.

    a = FoscDuration(3, 2);
    b = FoscDuration(1, 2);
    c = (a - b);
    c.str;

    
    Returns nonreduced fraction when expr is a nonreduced fraction.

    a = FoscDuration(3, 2);
    b = FoscNonreducedFraction(1, 2);
    c = (a - b);
    c.str;
    -------------------------------------------------------------------------------------------------------- */
    - { |expr|
        if (expr.isKindOf(FoscNonreducedFraction)) {
            ^(expr.neg + this);
        } {
            ^this.species.new(FoscFraction(this) - expr);
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    •
    def __truediv__(self, *args):
            r'''Documentation required.
            '''
            return self.__div__(*args)
    -------------------------------------------------------------------------------------------------------- */

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prGroupNonreducedFractionsByImpliedProlation

    @staticmethod
    def _group_nonreduced_fractions_by_implied_prolation(durations):
        durations = [
            mathtools.NonreducedFraction(duration)
            for duration in durations
            ]
        assert 0 < len(durations)
        group = [durations[0]]
        result = [group]
        for d in durations[1:]:
            d_f = set(mathtools.factors(d.denominator))
            d_f.discard(2)
            gd_f = set(mathtools.factors(group[0].denominator))
            gd_f.discard(2)
            if d_f == gd_f:
                group.append(d)
            else:
                group = [d]
                result.append(group)
        return result
    

    d = [[3, 4], [6, 8], [2, 4], [8, 16], [7, 14], [2, 3]];
    d = d.collect { |each| FoscDuration(each) };
    x = FoscDuration.prGroupNonreducedFractionsByImpliedProlation(d);
    x.do { |e| e.collect { |i| i.str }.postln };

    x = FoscDuration.prGroupNonreducedFractionsByImpliedProlation([1, 4] ! 13);
    x.do { |e| e.do { |i| i.str.postln } };
    -------------------------------------------------------------------------------------------------------- */
    *prGroupByImpliedProlation { |durations|
        var group, result, d_f, gd_f;
        durations = durations.collect { |duration| FoscNonreducedFraction(duration) };
        group = List[durations[0]];
        result = [group];
        durations[1..].do { |d|
            d_f = d.denominator.factors.as(Set);
            d_f.remove(2);
            gd_f = group[0].denominator.factors.as(Set);
            gd_f.remove(2);
            if (d_f == gd_f) {
                group.add(d);
            } {
                group = List[d];
                result = result.add(group);
            };
        };
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    •
    @staticmethod
    def _initialize_from_lilypond_duration_string(duration_string):
        numeric_body_strings = [str(2 ** n) for n in range(8)]
        other_body_strings = [r'\\breve', r'\\longa', r'\\maxima']
        body_strings = numeric_body_strings + other_body_strings
        body_strings = '|'.join(body_strings)
        pattern = r'^(%s)(\.*)$' % body_strings
        match = re.match(pattern, duration_string)
        if match is None:
            message = 'incorrect duration string format: {!r}.'
            message = message.format(duration_string)
            raise TypeError(message)
        body_string, dots_string = match.groups()
        try:
            body_denominator = int(body_string)
            body_duration = fractions.Fraction(1, body_denominator)
        except ValueError:
            if body_string == r'\breve':
                body_duration = fractions.Fraction(2)
            elif body_string == r'\longa':
                body_duration = fractions.Fraction(4)
            elif body_string == r'\maxima':
                body_duration = fractions.Fraction(8)
            else:
                message = 'unknown body string: {!r}.'
                message = message.format(body_string)
                raise ValueError(message)
        rational = body_duration
        for n in range(len(dots_string)):
            exponent = n + 1
            denominator = 2 ** exponent
            multiplier = fractions.Fraction(1, denominator)
            addend = multiplier * body_duration
            rational += addend
        return rational
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    •
    @staticmethod
    def _make_markup_score_block(selection):
        from abjad.tools import lilypondfiletools
        from abjad.tools import schemetools
        from abjad.tools import scoretools
        selection = copy.deepcopy(selection)
        staff = scoretools.Staff(selection)
        staff.context_name = 'RhythmicStaff'
        staff.remove_commands.append('Time_signature_engraver')
        staff.remove_commands.append('Staff_symbol_engraver')
        override(staff).stem.direction = Up
        #override(staff).stem.length = 4
        override(staff).stem.length = 5
        override(staff).tuplet_bracket.bracket_visibility = True
        override(staff).tuplet_bracket.direction = Up
        override(staff).tuplet_bracket.padding = 1.25
        override(staff).tuplet_bracket.shorten_pair = (-1, -1.5)
        scheme = schemetools.Scheme('tuplet-number::calc-fraction-text')
        override(staff).tuplet_number.text = scheme
        set_(staff).tuplet_full_length = True
        layout_block = lilypondfiletools.Block(name='layout')
        layout_block.indent = 0
        layout_block.ragged_right = True
        score = scoretools.Score([staff])
        override(score).spacing_spanner.spacing_increment = 0.5
        set_(score).proportional_notation_duration = False
        return score, layout_block
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    •
    @staticmethod
    def _to_score_markup(selection):
        from abjad.tools import markuptools
        staff, layout_block = Duration._make_markup_score_block(selection)
        command = markuptools.MarkupCommand('score', [staff, layout_block])
        markup = markuptools.Markup(command)
        return markup
    -------------------------------------------------------------------------------------------------------- */ 
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • dotCount

    Gets dot count.

    Dot count defined equal to number of dots required to notate duration.

    Raises assignability error when duration is not assignable.

    Returns positive integer.
    
    (1..16).do { |num|
        d = FoscDuration(num, 16);
        if (d.isAssignable) { d.str.post; Post.tab; d.dotCount.postln };
    };
    -------------------------------------------------------------------------------------------------------- */
    dotCount {
        ^if (this.isAssignable) {
            numerator.asBinaryString.select { |each| each == $1 }.size - 1;
        } { ^throw("%:%: duration % is not assignable.".format(this.species, thisMethod.name, this.str)) };
    }
    /* --------------------------------------------------------------------------------------------------------
    • equalOrGreaterAssignable

    Gets assignable duration equal to or just greater than this duration.
    
    Returns new duration.

    (1..16).do { |num|
        d = FoscDuration(num, 16);
        d.str.post;
        Post.tab;
        d.equalOrGreaterAssignable.str.postln;
    };
    -------------------------------------------------------------------------------------------------------- */
    equalOrGreaterAssignable {
        var goodDenominator, currentNumerator, candidate;
        if (denominator.isPowerOfTwo) {
            goodDenominator = denominator;
        } {
            goodDenominator = (denominator.nextPowerOf(2) / 2).asInteger;
        };
        currentNumerator = numerator;
        candidate = this.species.new(currentNumerator, goodDenominator);
        while { candidate.isAssignable.not } {
            currentNumerator = currentNumerator + 1;
            candidate = this.species.new(currentNumerator, goodDenominator);
        };
        ^candidate;
    }
    /* --------------------------------------------------------------------------------------------------------
    • equalOrGreaterPowerOfTwo

    Gets duration equal or just greater power of two.
    
    Returns new duration.


    (1..16).do { |num|
        d = FoscDuration(num, 16);
        d.str.post;
        Post.tab;
        d.equalOrGreaterPowerOfTwo.str.postln;
    };
    -------------------------------------------------------------------------------------------------------- */
    equalOrGreaterPowerOfTwo {
        ^this.species.new(this.asFloat.nextPowerOf(2));
    }
    /* --------------------------------------------------------------------------------------------------------
    • equalOrLesserAssignable

    Gets assignable duration equal or just less than this duration.
    
    Returns new duration.


    (1..16).do { |num|
        d = FoscDuration(num, 16);
        d.str.post;
        Post.tab;
        d.equalOrLesserAssignable.str.postln;
    };
    -------------------------------------------------------------------------------------------------------- */
    equalOrLesserAssignable {
        var goodDenominator, currentNumerator, candidate;
        goodDenominator = denominator.nextPowerOf(2).asInteger;
        currentNumerator = numerator;
        candidate = this.species.new(currentNumerator, goodDenominator);
        while { candidate.isAssignable.not } {
            currentNumerator = currentNumerator - 1;
            candidate = this.species.new(currentNumerator, goodDenominator);
        };
        ^candidate;
    }
    /* --------------------------------------------------------------------------------------------------------
    • equalOrLesserPowerOfTwo
    
    Gets duration equal to or just less than power of two.
    
    Returns new duration.


    (1..16).do { |num|
        d = FoscDuration(num, 16);
        d.str.post;
        Post.tab;
        d.equalOrLesserPowerOfTwo.str.postln;
    };
    -------------------------------------------------------------------------------------------------------- */
    equalOrLesserPowerOfTwo {
        if (this.asFloat == this.asFloat.nextPowerOf(2)) {
            ^this;
        } {
            ^this.species.new(this.asFloat.previousPowerOf(2));
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • flagCount

    Gets flag count. Flag count defined equal to number of flags required to notate duration.
    
    Returns nonnegative integer.

    (1..16).do { |num|
        d = FoscDuration(num, 64);
        d.str.post;
        Post.tab;
        d.flagCount.postln;
    };
    -------------------------------------------------------------------------------------------------------- */
    flagCount {
        ^max(floor(log2(numerator / denominator)).neg.asInteger - 2, 0);
    }
    /* --------------------------------------------------------------------------------------------------------
    • hasPowerOfTwoDenominator

    Is true when duration is an integer power of two. Otherwise false.
    

    FoscDuration(1, 4).hasPowerOfTwoDenominator;

    FoscDuration(1, 7).hasPowerOfTwoDenominator;
    -------------------------------------------------------------------------------------------------------- */
    hasPowerOfTwoDenominator {
        ^denominator.isPowerOfTwo;
    }
    /* --------------------------------------------------------------------------------------------------------
    • impliedProlation

    Gets implied prolation.

    Returns multipler.
    

    (1..16).do { |num|
        d = FoscDuration(1, num);
        d.str.post;
        Post.tab;
        d.impliedProlation.str.postln;
    };
    -------------------------------------------------------------------------------------------------------- */
    impliedProlation {
        var numerator;
        numerator = if (denominator.isPowerOfTwo) {
            denominator;
        } {
            denominator.previousPowerOf(2).asInteger;
        };
        ^FoscMultiplier(numerator, denominator);
    }
    /* --------------------------------------------------------------------------------------------------------
    • isAssignable


    Is true when duration is assignable. Otherwise false.
    
    Returns true or false.

 
    (1..16).do { |num|
        d = FoscDuration(num, 8);
        d.str.post;
        Post.tab;
        d.isAssignable.postln;
    };
    -------------------------------------------------------------------------------------------------------- */
    isAssignable {
        var index, bool;
        index = numerator.asBinaryString.indexOf($1);
        if (index.isNil) { ^false };
        bool = numerator.asBinaryString[index..].contains("01").not;
        bool = bool && denominator.isPowerOfTwo;
        bool = bool && (this > 0 && { this < 16 });
        ^bool;
    }
    /* --------------------------------------------------------------------------------------------------------
    • lilypondDurationString

    Gets LilyPond duration string. Raises assignability error when duration is not assignable.

    Returns string.
    
    FoscDuration(3, 16).lilypondDurationString;

    FoscDuration(5, 16).lilypondDurationString;     // assignability error
    -------------------------------------------------------------------------------------------------------- */
    lilypondDurationString {
        var undottedRational, undottedRationalStr, dotStr;
        if (this.isAssignable.not) {
            ^throw("%:%: duration % is not assignable.".format(this.species, thisMethod.name, this.str));
        };
        undottedRational = this.equalOrLesserPowerOfTwo;
        undottedRationalStr = case
        { undottedRational <= 1 } { undottedRational.denominator.asString }
        { undottedRational == 2 } { "\\breve" }
        { undottedRational == 4 } { "\\longa" }
        { undottedRational == 8 } { "\\maxima" }
        { ^throw("%: Cannot process undotted rational.").format(this.species) };
        dotStr = "".catList("." ! this.dotCount);
        ^(undottedRationalStr ++ dotStr);
    }
    /* --------------------------------------------------------------------------------------------------------
    • str (inherited in abjad)

    FoscDuration(1, 4).str;

    FoscDuration(inf).str;
    -------------------------------------------------------------------------------------------------------- */
    str {
        //^this.lilypondDurationString;
        case
        { this.numerator == inf } {
            ^"inf"
        }
        { this.numerator == -inf } {
            ^"-inf"
        }
        {
            ^"%/%".format(this.numerator, this.denominator);
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • pair
    
    Gets numerator and denominator.

    Returns integer pair.

    FoscDuration(3, 16).str;
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    •

    Gets prolation string.

    Returns string.
    
    (1..16).do { |num|
        d = FoscDuration(1, num);
        d.str.post;
        Post.tab;
        d.prolationString.postln;
    };
    -------------------------------------------------------------------------------------------------------- */
    prolationString {
        ^"%:%".format(denominator, numerator);
    }
    /* --------------------------------------------------------------------------------------------------------
    • reciprocal

    Gets reciprocal.
    

    Returns new duration.

    FoscDuration(1, 2).reciprocal.str;
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • durationsToNonreducedFractions

    Changes 'durations' to nonreduced fractions sharing least common denominator.


    • Example 1

    d = [FoscDuration(2, 4), 3, [5, 16]];
    f = FoscDuration.durationsToNonreducedFractions(d);
    f.do { |each| each.str.postln };
    -------------------------------------------------------------------------------------------------------- */
    *durationsToNonreducedFractions { |durations|
        var denominators, lcm, nonreducedFractions;
        durations = durations.collect { |each| FoscDuration(each) };
        denominators = durations.collect { |each| each.denominator };
        lcm = denominators.reduce('lcm');
        nonreducedFractions = durations.collect { |each|
            FoscNonreducedFraction(each).withDenominator(lcm);
        };
        ^nonreducedFractions;
    }
    /* --------------------------------------------------------------------------------------------------------
    • fromLilypondDurationString
    -------------------------------------------------------------------------------------------------------- */
    *fromLilypondDurationString {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • isToken
    -------------------------------------------------------------------------------------------------------- */
    *isToken {
        ^this.notYetImplemented(thisMethod);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • toClockString
    -------------------------------------------------------------------------------------------------------- */
    toClockString {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • toScoreMarkup
    -------------------------------------------------------------------------------------------------------- */
    toScoreMarkup {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • withDenominator 

    Changes duration to nonreduced fraction with denominator.
    
    Returns new duration.
    
    a = FoscDuration(1, 4);
    [4, 8, 16, 32].do { |each| a.withDenominator(each).str.postln };
    -------------------------------------------------------------------------------------------------------- */
}
