/* ------------------------------------------------------------------------------------------------------------
• Object
------------------------------------------------------------------------------------------------------------ */
+ Object {
    /* --------------------------------------------------------------------------------------------------------
    • assert

    a = FoscNote(60, [1, 4]);
    a.prGetTimespan_(200);
    -------------------------------------------------------------------------------------------------------- */
    assert { |message, receiver, method|
        var boolean;
        boolean = try { this.asBoolean } { false };
        if (boolean) { ^nil };
        case 
        { message.isNil } {
            if (method.isNil) {
                throw("%: assertion error".format(receiver.species));
            } {
                throw(
                    "%:%: assertion error: %."
                    .format(receiver.species, method.name, method.keyValuePairsFromArgs)
                );
            };
        }
        { message.notNil } {
            throw(message);
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • ccs

    compact compile string
    -------------------------------------------------------------------------------------------------------- */
    ccs {
        ^this.cs.removeWhiteSpace;
    }
}
