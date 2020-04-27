/* ------------------------------------------------------------------------------------------------------------
• FoscInterval

Abstract interval.

a = FoscInterval(4);
a.interval;
a.number;
------------------------------------------------------------------------------------------------------------ */
FoscInterval : FoscObject {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    _named_interval_quality_abbreviation_regex_body = '''
        (M|         # major
        m|          # minor
        P|          # perfect
        aug|        # augmented
        dim)        # dimished
        '''

    _named_interval_quality_abbreviation_regex = re.compile(
        '^{}$'.format(_named_interval_quality_abbreviation_regex_body),
        re.VERBOSE,
        )

    _interval_name_abbreviation_regex_body = '''
        ([+,-]?)    # one plus, one minus, or neither
        {}          # exactly one quality abbreviation
        (\d+)       # followed by one or more digits
        '''.format(
        _named_interval_quality_abbreviation_regex_body,
        )

    _interval_name_abbreviation_regex = re.compile(
        '^{}$'.format(_interval_name_abbreviation_regex_body),
        re.VERBOSE,
        )
    -------------------------------------------------------------------------------------------------------- */
    classvar <namedIntervalQualityAbbreviationRegexBody, <namedIntervalQualityAbbreviationRegex;
    classvar <intervalNameAbbreviationRegexBody, <intervalNameAbbreviationRegex;
    var <interval, <number;
    *initClass {
        namedIntervalQualityAbbreviationRegexBody = "(M|m|P|aug|dim)";
        namedIntervalQualityAbbreviationRegex = "^%$".format(namedIntervalQualityAbbreviationRegexBody);
        intervalNameAbbreviationRegexBody = "([+,-]?)%(\\d+)"
            .format(namedIntervalQualityAbbreviationRegexBody);
        intervalNameAbbreviationRegex = "^%$".format(intervalNameAbbreviationRegexBody);
    }
    /* --------------------------------------------------------------------------------------------------------
    •
    
    @abc.abstractmethod
    def __init__(self):
        pass
    -------------------------------------------------------------------------------------------------------- */
    *new { |interval|
        if (interval.isKindOf(FoscInterval)) { ^interval };
        ^super.new.init(interval);
    }
    init { |argInterval|
        interval = argInterval;
        number = argInterval;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • abs

    Gets absolute value of interval.

    Returns new interval.
    
    def __abs__(self):
        return type(self)(abs(self.number))
    -------------------------------------------------------------------------------------------------------- */
    abs {
        ^this.species.new(this.number.abs);
    }
    /* --------------------------------------------------------------------------------------------------------
    • float

    Coerce to float.

    Returns float.
    
    def __float__(self):
        return float(self.number)
    -------------------------------------------------------------------------------------------------------- */
    asFloat {
        ^this.number.asFloat;
    }
    /* --------------------------------------------------------------------------------------------------------
    • <

    Is true when interval is less than argument.

    Returns true or false.
    
    @abc.abstractmethod
    def __lt__(self, argument):
        raise NotImplementedError
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    • neg

    Negates interval.

    Returns interval.
    
    def __neg__(self):
        pass
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    • str

    Gets string representation of interval.

    Returns string.
    
    def __str__(self):
        return str(self.number)
    -------------------------------------------------------------------------------------------------------- */
    str {
        ^this.number.asString;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prDirectionSymbol
    
    def _get_direction_symbol(self):
        if self.direction_number == -1:
            return '-'
        elif self.direction_number == 0:
            return ''
        elif self.direction_number == 1:
            return '+'
        else:
            raise ValueError
    -------------------------------------------------------------------------------------------------------- */
    prDirectionSymbol {
        switch(this.directionNumber,
            -1, { ^"-" },
            0, { ^"" },
            1, { ^"+ "}
        );
        throw("%:%: value error: %".format(this.species, thisMethod.name, this.directionNumber));
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • (abjad: cents)

    Gets cents of interval.

    Returns nonnegative number.
    
    @property
    def cents(self):
        return 100 * self.semitones
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • isNamedIntervalAbbreviation

    Is true when argument is a named interval abbreviation. Otherwise false.

    Underlying regex: "^([+,-]?)(M|m|P|aug|dim)(\\d+)$"

    Returns true or false.


    • Example 1
    FoscInterval.isNamedIntervalAbbreviation("+M9");

    @staticmethod
    def is_named_interval_abbreviation(argument):
        if not isinstance(argument, str):
            return False
        return bool(Interval._interval_name_abbreviation_regex.match(argument))
    -------------------------------------------------------------------------------------------------------- */
    *isNamedIntervalAbbreviation { |expr|
        if (expr.isString.not && { expr.isKindOf(Symbol).not }) { ^false };
        ^FoscInterval.intervalNameAbbreviationRegex.matchRegexp(expr.asString);
    }
    /* --------------------------------------------------------------------------------------------------------
    • isNamedQualityAbbreviation

    Is true when argument is a named-interval quality abbreviation. Otherwise false.

    Underlying regex: "^(M|m|P|aug|dim)$"

    Returns true or false.


    • Example 1
    FoscInterval.isNamedQualityAbbreviation("aug");

    
    @staticmethod
    def is_named_interval_quality_abbreviation(argument):
        if not isinstance(argument, str):
            return False
        return bool(Interval._named_interval_quality_abbreviation_regex.match(
            argument))
    -------------------------------------------------------------------------------------------------------- */
    *isNamedQualityAbbreviation { |expr|
        if (expr.isString.not && { expr.isKindOf(Symbol).not }) { ^false };
        ^FoscInterval.namedIntervalQualityAbbreviationRegex.matchRegexp(expr.asString);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • transpose

    Transposes pitch_carrier by interval.

    Returns new pitch carrier.
   
    @abc.abstractmethod
    def transpose(self, pitch_carrier):
        raise NotImplementedError
    -------------------------------------------------------------------------------------------------------- */
    transpose { |pitchCarrier|

    }
}
