/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

function AuthorLookup(url, authorityInput, collectionID) {
//    TODO i18n
//alert("collection" + collectionID);

    $(".authorlookup").remove();
    var content =   $(
                    '<div class="authorlookup modal fade" tabindex="-1" role="dialog" aria-labelledby="personLookupLabel" aria-hidden="true">' +
                        '<div class="modal-dialog">'+
                            '<div class="modal-content">'+
                                '<div class="modal-header">'+
									'<a class="close" href="JavaScript:window.close()">'+
//									'<button class="close" data-dismiss="modal" type="button">'+
									'<span aria-hidden="true">×</span>'+
									'<span class="sr-only">Close</span></a>'+
	//								'</button>'+
                                    //'<button type="button" class="close" onClick="return targetopener(this,true,true)"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>'+
                                    '<h4 class="modal-title" id="personLookupLabel"></h4>'+
                                '</div>'+
                                '<div class="modal-body">'+
                                    '<div title="Person Lookup">' +
                                        '<table class="dttable col-xs-4">' +
                                            '<thead>' +
                                                '<th>Name</th>' +
                                            '</thead>' +
                                            '<tbody>' +
                                                '<tr><td>Loading...<td></tr>' +
                                            '</tbody>' +
                                        '</table>' +
                                        '<span class="no-vcard-selected">There\'s no one selected</span>' +
                                        '<ul class="vcard list-group" style="display: none;">' +
                                            '<li><ul class="variable"/></li  class="list-group-item">'+
                                            '<li class="vcard-insolr list-group-item">' +
                                                '<label>Items in this repository:&nbsp;</label>' +
                                                '<span/>' +
                                            '</li>' +
                                            '<li class="vcard-add list-group-item">' +
                                                '<input class="ds-button-field btn btn-default" value="Add This Person" type="button"/>' +
                                            '</li>' +
                                        '</ul>' +
                                    '</div>'+
                                '</div>'+
                            '</div>'+
                        '</div>'+
                    '</div>'
                    );

    var moreButton = '<button id="lookup-more-button" class="btn btn-default">show more</button>';
    var lessButton = '<button id="lookup-less-button" class="btn btn-default">show less</button>';
    var button = moreButton;

    var datatable = content.find("table.dttable");
    datatable.dataTable({
        "aoColumns": [
            {
                "bSortable": false,
                "sWidth": "200px"
            },
            {
                "bSortable": false,
                "bSearchable": false,
                "bVisible": false
            }
        ],
        "oLanguage": {
            "sInfo": 'Showing _START_ to _END_ of _TOTAL_ people',
            "sInfoEmpty": 'Showing 0 to 0 of 0 people',
            "sInfoFiltered": '(filtered from _MAX_ total people)',
            "sLengthMenu": '_MENU_ people/page',
            "sZeroRecords": 'No people found'
        },
        "bAutoWidth": false,
        "bJQueryUI": true,
        "bProcessing": true,
        "bSort": false,
        "bPaginate": false,
        "sPaginationType": "two_button",
        "bServerSide": true,
        "sAjaxSource": url,
        "sDom": '<"H"lfr><"clearfix"t<"vcard-wrapper col-xs-8">><"F"ip>',
        "fnInitComplete": function() {
            content.find("table.dttable").show();
            content.find("div.vcard-wrapper").append(content.find('.no-vcard-selected')).append(content.find('ul.vcard'));
            content.modal();

            content.find('.dataTables_wrapper').parent().attr('style', 'width: auto; min-height: 121px; height: auto;');
            var searchFilter = content.find('.dataTables_filter input');
            var initialInput = "";
            if (authorityInput.indexOf('value_') != -1) { // edit item
                initialInput = $('textarea[name=' + authorityInput + ']').val();
            } else {   // submission
                var lastName = $('input[name=' + authorityInput + '_last]');
                if (lastName.size()) { // author input type
                    initialInput = (lastName.val() + " " + $('input[name=' + authorityInput + '_first]').val()).trim();
                } else { // other input types
                    initialInput = $('input[name=' + authorityInput + ']').val();
                }
            }
            searchFilter.val(initialInput);
            setTimeout(function () {
                searchFilter.trigger($.Event("keyup", { keyCode: 13 }));
            }, 50);
            searchFilter.trigger($.Event("keyup", { keyCode: 13 }));
            searchFilter.addClass('form-control');
            content.find('.ui-corner-tr').removeClass('.ui-corner-tr');
            content.find('.ui-corner-tl').removeClass('.ui-corner-tl');

        },
        "fnInfoCallback": function( oSettings, iStart, iEnd, iMax, iTotal, sPre ) {
          return "Showing "+ iEnd + " results. "+button;
        },
        "fnRowCallback": function( nRow, aData, iDisplayIndex ) {
            aData = aData[1];
            var $row = $(nRow);

            var authorityID = $(this).closest('.dataTables_wrapper').find('.vcard-wrapper .vcard').data('authorityID');
            if (authorityID != undefined && aData['authority'] == authorityID) {
                $row.addClass('current-item');
            }

            $row.addClass('clickable');
            if(aData['insolr']=="false"){
                $row.addClass("notinsolr");
            }

            $row.click(function() {
                var $this = $(this);
                $this.siblings('.current-item').removeClass('current-item');
                $this.addClass('current-item');
                var wrapper = $this.closest('.dataTables_wrapper').find('.vcard-wrapper');
                wrapper.find('.no-vcard-selected:visible').hide();
                var vcard = wrapper.find('.vcard');
                vcard.data('authorityID', aData['authority']);
                vcard.data('name', aData['value']);

                var notDisplayed = ['insolr','value','authority'];
                var predefinedOrder = ['last-name','first-name'];
                var variable = vcard.find('.variable');
                variable.empty();
                predefinedOrder.forEach(function (entry) {
                    variableItem(aData, entry, variable);
                });

                for (var key in aData) {
                    if (aData.hasOwnProperty(key) && notDisplayed.indexOf(key) < 0 && predefinedOrder.indexOf(key) < 0) {
                        variableItem(aData, key, variable);
                    }
                }

                function variableItem(aData, key, variable) {
                    var label = key.replace(/-/g, ' ');
                    var dataString = '';
                    dataString += '<li class="vcard-' + key + '">' +
                        '<label>' + label + ': </label>';

                    if(key == 'orcid'){
                        dataString +='<span><a target="_blank" href="http://orcid.org/' + aData[key] + '">' + aData[key] + '</a></span>';
                    } else {
                        dataString += '<span>' + aData[key] + '</span>';
                    }
                    dataString += '</li>';

                    variable.append(dataString);
                    return label;
                }
                
                if(aData['insolr']!="false"){
                   // var discoverLink = window.DSpace.context_path + "/discover?filtertype=author&filter_relational_operator=authority&filter=" + aData['insolr'];
				   var discoverLink = "http://dspacetestes.bg.uc.pt:8080/jspui/simple-search?location=&query=&filtername=author&filtertype=authority&filterquery=" + aData['insolr'].trim() + "				   &rpp=10&sort_by=score&order=desc";
                    vcard.find('.vcard-insolr span').empty().append('<a href="'+ discoverLink+'" target="_new">view items</a>');
                }else{
                    vcard.find('.vcard-insolr span').text("0");
                }
                vcard.find('.vcard-add input').click(function() {
                    if (authorityInput.indexOf('value_') != -1) {
                        // edit item
                        $('input[name=' + authorityInput + ']').val(vcard.find('.vcard-last-name span').text() + ', ' + vcard.find('.vcard-first-name span').text());
                        var oldAuthority = $('input[name=' + authorityInput + '_authority]');
                        oldAuthority.val(vcard.data('authorityID'));
                        $('textarea[name='+ authorityInput+']').val(vcard.data('name'));
                    } else {
                        // submission
						//alert("hacking it");


						// "this" is the window,
						var form = document.getElementById('aspect_general_ChoiceLookupTransformer_div_lookup');
						//var select = form.elements['chooser'];
						//var so = select.options[select.selectedIndex];
						//var isName = form.elements['paramIsName'].value == 'true';
					
						//if (isName)
						//{
							//form.elements['text1'].value = "teste";
						//	form.elements['text2'].value = firstNameOf(so.value);
						//}
						//else
						//	form.elements['text1'].value = so.value;

						var authorityID = $(this).closest('.dataTables_wrapper').find('.vcard-wrapper .vcard').data('authorityID');
						//alert("id="+ authorityID);
						//alert(vcard.find('.vcard-first-name span').text());
						
						form.elements['text2'].value = vcard.find('.vcard-first-name span').text();
						form.elements['text1'].value = vcard.find('.vcard-last-name span').text();
						form.elements['text3'].value = vcard.find('.vcard-orcid span').text();
						form.elements['text4'].value = authorityID;
						//fim hacking
						//alert("id2="+ authorityID);
                        var lastName = $('input[name=' + authorityInput + '_last]');
                        if (lastName.size()) { // author input type
                            lastName.val(vcard.find('.vcard-last-name span').text());
                            $('input[name=' + authorityInput + '_first]').val(vcard.find('.vcard-first-name span').text());
                        }
                        else { // other input types
                            $('input[name=' + authorityInput + ']').val(vcard.data('name'));
                        }

                        $('input[name=' + authorityInput + '_authority]').val(vcard.data('authorityID'));
                        
						//alert("id3="+ authorityID);
						//$('input[name=submit_'+ authorityInput +'_add]').click();
						$('input[name=accept]').click();
                    }
					
					
						
                    //content.modal('hide');
                });
                vcard.show();
            });

            return nRow;
        },
        "fnDrawCallback": function() {
            var wrapper = $(this).closest('.dataTables_wrapper');
            if (wrapper.find('.current-item').length > 0) {
                wrapper.find('.vcard-wrapper .no-vcard-selected:visible').hide();
                wrapper.find('.vcard-wrapper .vcard:hidden').show();
            }
            else {
                wrapper.find('.vcard-wrapper .vcard:visible').hide();
                wrapper.find('.vcard-wrapper .no-vcard-selected:hidden').show();
            }
            $('#lookup-more-button').click(function () {
                button = lessButton;
                datatable.fnFilter($('.dataTables_filter > input').val());
            });
            $('#lookup-less-button').click(function () {
                button = moreButton;
                datatable.fnFilter($('.dataTables_filter > input').val());
            });
        },
        "fnServerData": function (sSource, aoData, fnCallback) {
            var sEcho;
            var query;
            var start;
            var limit;

            $.each(aoData, function() {
                if (this.name == "sEcho") {
                    sEcho = this.value;
                }
                else if (this.name == "sSearch") {
                    query = this.value;
                }
                else if (this.name == "iDisplayStart") {
                    start = this.value;
                }
                else if (this.name == "iDisplayLength") {
                    limit = this.value;
                }
            });

            if (collectionID == undefined) {
                collectionID = '-1';
            }

            if (sEcho == undefined) {
                sEcho = '';
            }

            if (query == undefined) {
                query = '';
            }

            if (start == undefined) {
                start = '0';
            }

            if (limit == undefined) {
                limit = '0';
            }

            if (button == lessButton) {
                limit = '20';
            }
            if (button == moreButton) {
                limit = '10';
            }


            var data = [];
            data.push({"name": "query", "value": query});
            data.push({"name": "collection", "value": collectionID});
            data.push({"name": "start", "value": start});
            data.push({"name": "limit", "value": limit});

            var $this = $(this);

            $.ajax({
                cache: false,
                url: sSource,
                dataType: 'xml',
                data: data,
                success: function (data) {
                    /* Translate AC XML to DT JSON */
                    var $xml = $(data);
                    var aaData = [];
                    $.each($xml.find('Choice'), function() {
                        // comes from org.dspace.content.authority.SolrAuthority.java
                        var choice = this;

                        var row = [];
                        var rowData = {};

                        for(var k = 0; k < choice.attributes.length; k++) {
                            var attr = choice.attributes[k];
                            rowData[attr.name] = attr.value;
                        }

                        row.push(rowData.value);
                        row.push(rowData);
                        aaData.push(row);

                    });

                    var nbFiltered = $xml.find('Choices').attr('total');

                    var total = $this.data('totalNbPeople');
                    if (total == undefined || (total * 1) < 1) {
                        total = nbFiltered;
                        $this.data('totalNbPeople', total);
                    }

                    var json = {
                        "sEcho": sEcho,
                        "iTotalRecords": total,
                        "iTotalDisplayRecords": nbFiltered,
                        "aaData": aaData
                    };
                    fnCallback(json);
                }
            });
        }
    });
}
