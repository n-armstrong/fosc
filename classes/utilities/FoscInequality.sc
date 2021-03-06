/* ------------------------------------------------------------------------------------------------------------
• FoscInequality
------------------------------------------------------------------------------------------------------------ */
FoscInequality : FoscObject {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    classvar operatorSymbols;
    var <operatorSymbol, <operatorFunction;
    *initClass {
        operatorSymbols = #['!=', '<', '<=', '==', '>', '>=']; 
    }
    *new { |operatorSymbol='<'|
        assert(
            operatorSymbols.includes(operatorSymbol.asSymbol),
            "%:new: not a valid operator symbol: %.".format(this.name, operatorSymbol);
        );
        ^super.new.init(operatorSymbol);
    }
    init { |argOperatorSymbol|
        operatorSymbol = argOperatorSymbol;
        // self._operator_function = {
        //     '!=': operator.ne,
        //     '<': operator.lt,
        //     '<=': operator.le,
        //     '==': operator.eq,
        //     '>': operator.gt,
        //     '>=': operator.ge,
        //     }[self._operator_string]
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • value
    -------------------------------------------------------------------------------------------------------- */
    value {
        ^this.subclassResponsibility(thisMethod);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • operatorSymbol
    -------------------------------------------------------------------------------------------------------- */
}