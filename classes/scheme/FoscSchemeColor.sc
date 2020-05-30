/* ------------------------------------------------------------------------------------------------------------
• FoscSchemeColor

a = FoscSchemeColor('ForestGreen');
a.format;

FoscSchemeColor.colorNames;
• see: https://en.wikipedia.org/wiki/X11_color_names


m = FoscMarkup("foobar");
m = m.withColor(FoscSchemeColor("ForestGreen"));
m.format;

a = FoscNote(60, 1/4);
override(a).noteHead.color = FoscSchemeColor('ForestGreen');
a.format;
a.show;
------------------------------------------------------------------------------------------------------------ */
FoscSchemeColor : FoscScheme {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    *colorNames {
        ^#[
            'snow',
            'GhostWhite',
            'WhiteSmoke',
            'gainsboro',
            'FloralWhite',
            'OldLace',
            'linen',
            'AntiqueWhite',
            'PapayaWhip',
            'BlanchedAlmond',
            'bisque',
            'PeachPuff',
            'NavajoWhite',
            'moccasin',
            'cornsilk',
            'ivory',
            'LemonChiffon',
            'seashell',
            'honeydew',
            'MintCream',
            'azure',
            'AliceBlue',
            'lavender',
            'LavenderBlush',
            'MistyRose',
            'white',
            'black',
            'DarkSlateGrey',
            'DimGrey',
            'SlateGrey',
            'LightSlateGrey',
            'grey',
            'LightGrey',
            'MidnightBlue',
            'navy',
            'NavyBlue',
            'CornflowerBlue',
            'DarkSlateBlue',
            'SlateBlue',
            'MediumSlateBlue',
            'LightSlateBlue',
            'MediumBlue',
            'RoyalBlue',
            'blue',
            'DodgerBlue',
            'DeepSkyBlue',
            'SkyBlue',
            'LightSkyBlue',
            'SteelBlue',
            'LightSteelBlue',
            'LightBlue',
            'PowderBlue',
            'PaleTurquoise',
            'DarkTurquoise',
            'MediumTurquoise',
            'turquoise',
            'cyan',
            'LightCyan',
            'CadetBlue',
            'MediumAquamarine',
            'aquamarine',
            'DarkGreen',
            'DarkOliveGreen',
            'DarkSeaGreen',
            'SeaGreen',
            'MediumSeaGreen',
            'LightSeaGreen',
            'PaleGreen',
            'SpringGreen',
            'LawnGreen',
            'green',
            'chartreuse',
            'MediumSpringGreen',
            'GreenYellow',
            'LimeGreen',
            'YellowGreen',
            'ForestGreen',
            'OliveDrab',
            'DarkKhaki',
            'khaki',
            'PaleGoldenrod',
            'LightGoldenrodYellow',
            'LightYellow',
            'yellow',
            'gold',
            'LightGoldenrod',
            'goldenrod',
            'DarkGoldenrod',
            'RosyBrown',
            'IndianRed',
            'SaddleBrown',
            'sienna',
            'peru',
            'burlywood',
            'beige',
            'wheat',
            'SandyBrown',
            'tan',
            'chocolate',
            'firebrick',
            'brown',
            'DarkSalmon',
            'salmon',
            'LightSalmon',
            'orange',
            'DarkOrange',
            'coral',
            'LightCoral',
            'tomato',
            'OrangeRed',
            'red',
            'HotPink',
            'DeepPink',
            'pink',
            'LightPink',
            'PaleVioletRed',
            'maroon',
            'MediumVioletRed',
            'VioletRed',
            'magenta',
            'violet',
            'plum',
            'orchid',
            'MediumOrchid',
            'DarkOrchid',
            'DarkViolet',
            'BlueViolet',
            'purple',
            'MediumPurple',
            'thistle',
            'DarkGrey',
            'DarkBlue',
            'DarkCyan',
            'DarkMagenta',
            'DarkRed',
            'LightGreen'
        ];
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
	• prGetFormattedValue
	-------------------------------------------------------------------------------------------------------- */
	prGetFormattedValue {
		^"(x11-color '%)".format(value);
	}
}