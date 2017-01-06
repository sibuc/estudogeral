#!/usr/bin/perl -w

# First install  JSON::Parse Test::Differences ; CSS::Sass, example: cpan install CSS::Sass

use Data::Dumper;               # Perl core module
use strict;                     # Good practice
use warnings;                   # Good practice
use CSS::Sass;
use JSON::Parse  'json_file_to_perl';



# Object Oriented API
my $json_fromfile = json_file_to_perl ('config.codekit');
my $sass = CSS::Sass->new (output_style    => SASS_STYLE_COMPRESSED,);

#print Dumper \$json_fromfile->{files}->{'/bower_components/boostrap-sass/assets/stylesheets/dspace.scss'};
#print Dumper keys ($json_fromfile->{files});

foreach my $file (keys ($json_fromfile->{files})) {

    # if file has .scss extension
    if (lc $file =~ /.scss$/) {

        my $item = $json_fromfile->{files}->{$file};

        # if ignore parameter is defined
        if ($item->{ignore} != 1) {
            
            # Call file compilation (may die on errors)
            print STDERR "Compiling: .$item->{inputAbbreviatedPath}\n";
            my $css = $sass->compile_file(".".$item->{inputAbbreviatedPath});

            # Save css file
            print STDERR "Saving to: .$item->{outputAbbreviatedPath}\n";
            open(my $fh, '>', ".$item->{outputAbbreviatedPath}")
                or die "Couldn't save to file '.$item->{outputAbbreviatedPath}'";
            print $fh $css;
            close $fh;
        }

    }
    #print Dumper \$json_fromfile->{files}->{$file};
}




