/* --------------------------------------------------------------------------------------------------------
• LilypondDrums

abjad: abjad/ly/drums.py

LilypondDrums.list.printAll;
LilypondDrums.includes('snare');
LilypondDrums.includes('loagogo');
LilypondDrums.at('bolm');
-------------------------------------------------------------------------------------------------------- */
LilypondDrums {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    classvar <list, <lilypondVersion='2.19.24', dict;
    *initClass {
        dict = this.initItems;
        list = dict.keys.as(Array).sort;
    }
    *initItems {
        ^(
            'acousticbassdrum': 'acousticbassdrum',
            'acousticsnare': 'acousticsnare',
            'agh': 'hiagogo',
            'agl': 'loagogo',
            'bassdrum': 'bassdrum',
            'bd': 'bassdrum',
            'bda': 'acousticbassdrum',
            'boh': 'hibongo',
            'bohm': 'mutehibongo',
            'boho': 'openhibongo',
            'bol': 'lobongo',
            'bolm': 'mutelobongo',
            'bolo': 'openlobongo',
            'cab': 'cabasa',
            'cabasa': 'cabasa',
            'cb': 'cowbell',
            'cgh': 'hiconga',
            'cghm': 'mutehiconga',
            'cgho': 'openhiconga',
            'cgl': 'loconga',
            'cglm': 'muteloconga',
            'cglo': 'openloconga',
            'chinesecymbal': 'chinesecymbal',
            'cl': 'claves',
            'claves': 'claves',
            'closedhihat': 'closedhihat',
            'cowbell': 'cowbell',
            'crashcymbal': 'crashcymbal',
            'crashcymbala': 'crashcymbala',
            'crashcymbalb': 'crashcymbalb',
            'cuim': 'mutecuica',
            'cuio': 'opencuica',
            'cymc': 'crashcymbal',
            'cymca': 'crashcymbala',
            'cymcb': 'crashcymbalb',
            'cymch': 'chinesecymbal',
            'cymr': 'ridecymbal',
            'cymra': 'ridecymbala',
            'cymrb': 'ridecymbalb',
            'cyms': 'splashcymbal',
            'da': 'onedown',
            'db': 'twodown',
            'dc': 'threedown',
            'dd': 'fourdown',
            'de': 'fivedown',
            'electricsnare': 'electricsnare',
            'fivedown': 'fivedown',
            'fiveup': 'fiveup',
            'fourdown': 'fourdown',
            'fourup': 'fourup',
            'gui': 'guiro',
            'guil': 'longguiro',
            'guiro': 'guiro',
            'guis': 'shortguiro',
            'halfopenhihat': 'halfopenhihat',
            'handclap': 'handclap',
            'hc': 'handclap',
            'hh': 'hihat',
            'hhc': 'closedhihat',
            'hhho': 'halfopenhihat',
            'hho': 'openhihat',
            'hhp': 'pedalhihat',
            'hiagogo': 'hiagogo',
            'hibongo': 'hibongo',
            'hiconga': 'hiconga',
            'highfloortom': 'highfloortom',
            'hightom': 'hightom',
            'hihat': 'hihat',
            'himidtom': 'himidtom',
            'hisidestick': 'hisidestick',
            'hitimbale': 'hitimbale',
            'hiwoodblock': 'hiwoodblock',
            'loagogo': 'loagogo',
            'lobongo': 'lobongo',
            'loconga': 'loconga',
            'longguiro': 'longguiro',
            'longwhistle': 'longwhistle',
            'losidestick': 'losidestick',
            'lotimbale': 'lotimbale',
            'lowfloortom': 'lowfloortom',
            'lowmidtom': 'lowmidtom',
            'lowoodblock': 'lowoodblock',
            'lowtom': 'lowtom',
            'mar': 'maracas',
            'maracas': 'maracas',
            'mutecuica': 'mutecuica',
            'mutehibongo': 'mutehibongo',
            'mutehiconga': 'mutehiconga',
            'mutelobongo': 'mutelobongo',
            'muteloconga': 'muteloconga',
            'mutetriangle': 'mutetriangle',
            'onedown': 'onedown',
            'oneup': 'oneup',
            'opencuica': 'opencuica',
            'openhibongo': 'openhibongo',
            'openhiconga': 'openhiconga',
            'openhihat': 'openhihat',
            'openlobongo': 'openlobongo',
            'openloconga': 'openloconga',
            'opentriangle': 'opentriangle',
            'pedalhihat': 'pedalhihat',
            'rb': 'ridebell',
            'ridebell': 'ridebell',
            'ridecymbal': 'ridecymbal',
            'ridecymbala': 'ridecymbala',
            'ridecymbalb': 'ridecymbalb',
            'shortguiro': 'shortguiro',
            'shortwhistle': 'shortwhistle',
            'sidestick': 'sidestick',
            'sn': 'snare',
            'sna': 'acousticsnare',
            'snare': 'snare',
            'sne': 'electricsnare',
            'splashcymbal': 'splashcymbal',
            'ss': 'sidestick',
            'ssh': 'hisidestick',
            'ssl': 'losidestick',
            'tamb': 'tambourine',
            'tambourine': 'tambourine',
            'threedown': 'threedown',
            'threeup': 'threeup',
            'timh': 'hitimbale',
            'timl': 'lotimbale',
            'tomfh': 'highfloortom',
            'tomfl': 'lowfloortom',
            'tomh': 'hightom',
            'toml': 'lowtom',
            'tommh': 'himidtom',
            'tomml': 'lowmidtom',
            'tri': 'triangle',
            'triangle': 'triangle',
            'trim': 'mutetriangle',
            'trio': 'opentriangle',
            'tt': 'tamtam',
            'twodown': 'twodown',
            'twoup': 'twoup',
            'ua': 'oneup',
            'ub': 'twoup',
            'uc': 'threeup',
            'ud': 'fourup',
            'ue': 'fiveup',
            'vibraslap': 'vibraslap',
            'vibs': 'vibraslap',
            'wbh': 'hiwoodblock',
            'wbl': 'lowoodblock',
            'whl': 'longwhistle',
            'whs': 'shortwhistle'
        );
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    *at { |name|
        ^dict[name];
    }
    *includes { |name|
        ^list.includes(name.asSymbol);
    }
}
