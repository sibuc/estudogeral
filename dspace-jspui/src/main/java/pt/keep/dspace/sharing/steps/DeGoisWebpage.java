package pt.keep.dspace.sharing.steps;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;
import org.dspace.content.DCDate;
import org.dspace.content.Metadatum;
import org.dspace.content.Item;
import org.dspace.core.ConfigurationManager;
import org.um.dsi.gavea.wsrcaap.request.rcaap.APRESENTACAOORALDETRABALHOType;
import org.um.dsi.gavea.wsrcaap.request.rcaap.ARTIGOCIENTIFICOType;
import org.um.dsi.gavea.wsrcaap.request.rcaap.CAPITULODELIVROPUBLICADOType;
import org.um.dsi.gavea.wsrcaap.request.rcaap.ElAreasConhec;
import org.um.dsi.gavea.wsrcaap.request.rcaap.ElPalChave;
import org.um.dsi.gavea.wsrcaap.request.rcaap.LIVROPUBLICADOType;
import org.um.dsi.gavea.wsrcaap.request.rcaap.OUTRAPRODUCAOCIENTIFICAType;
import org.um.dsi.gavea.wsrcaap.request.rcaap.PRODUCAO;
import org.um.dsi.gavea.wsrcaap.request.rcaap.RELATORIODEINVESTIGACAOType;
import org.um.dsi.gavea.wsrcaap.request.rcaap.TEXTOEMJORNALOUREVISTAType;
import org.um.dsi.gavea.wsrcaap.request.rcaap.ARTIGOCIENTIFICOType.AUTORES;
import org.um.dsi.gavea.wsrcaap.request.rcaap.ARTIGOCIENTIFICOType.DADOSDAPUBLICACAO;
import org.um.dsi.gavea.wsrcaap.request.rcaap.ARTIGOCIENTIFICOType.DADOSDOARTIGO;
import org.um.dsi.gavea.wsrcaap.request.rcaap.CAPITULODELIVROPUBLICADOType.DADOSBASICOSDOCAPITULO;
import org.um.dsi.gavea.wsrcaap.request.rcaap.CAPITULODELIVROPUBLICADOType.DESCRICAODOCAPITULO;
import org.um.dsi.gavea.wsrcaap.request.rcaap.ElAreasConhec.AREADOCONHECIMENTO;
import org.um.dsi.gavea.wsrcaap.request.rcaap.ElPalChave.PALAVRACHAVE;
import org.um.dsi.gavea.wsrcaap.request.rcaap.LIVROPUBLICADOType.DADOSBASICOSDOLIVRO;
import org.um.dsi.gavea.wsrcaap.request.rcaap.LIVROPUBLICADOType.DESCRICAODOLIVRO;
import org.um.dsi.gavea.wsrcaap.request.rcaap.OUTRAPRODUCAOCIENTIFICAType.DADOSBASICOS;
import org.um.dsi.gavea.wsrcaap.request.rcaap.OUTRAPRODUCAOCIENTIFICAType.DESCRICAO;
import org.um.dsi.gavea.wsrcaap.request.researcher.INVESTIGADOR;

import pt.keep.degois.xml.Util;
import pt.keep.dspace.sharing.ItemWrapper;
import pt.keep.dspace.sharing.handlers.RequestHandler;

public class DeGoisWebpage extends RequestHandler {
	public static final int STATUS_INVALID_USER = 1;
	public static final int STATUS_CANNOT_SEND = 2;
	public static final int STATUS_RETURN = 3;
	public static final int STATUS_SKIP = 4;

	public static final int KEYWORDS_LIST_SIZE = 6;

	public static final String SUBMIT_DEGOIS_REQUEST = "submit_send_degois";
	public static final String SUBMIT_SKIP_BUTTON = "submit_skip_degois";
	public static final String KNOWN_DCTYPES_FILE = "degois.map";

	public static final String CONFIG_ACTIVE = "degois.active";
	public static final String CONFIG_URL = "degois.url";
	public static final String CONFIG_REP_ID = "degois.repository.id";
	public static final String CONFIG_SITUATION = "degois.default.situation";
	public static final String CONFIG_DSPACE_TITLE = "degois.default.dspace.title";
	public static final String CONFIG_DEGOIS_TITLE = "degois.default.degois.title";
	public static final String CONFIG_PUBLISHER = "degois.default.publisher";
	public static final String CONFIG_NUMBER = "degois.default.number";
	public static final String CONFIG_FIRSTPAGE = "degois.default.first.page";
	public static final String CONFIG_LASTPAGE = "degois.default.last.page";
	public static final String CONFIG_LOCATION = "degois.default.location";
	public static final String CONFIG_LANGUAGE = "degois.default.language";
	public static final String CONFIG_AUTHOR = "degois.default.author";
	public static final String CONFIG_JORNALTYPE = "degois.default.journal.type";
	public static final String CONFIG_PRESENTATIONTYPE = "degois.default.presentation.type";

	/** log4j logger */
	private static Logger log = Logger.getLogger(DeGoisWebpage.class);
	private HttpServletRequest request;
    private HttpServletResponse response;

	@Override
	public void doProcessing(HttpServletRequest request,
			HttpServletResponse response, ItemWrapper item) {
		this.request = request;
		this.response = response;

		this.process(item);
	}

	private String getValue(Metadatum[] values, String default_value) {
		if (values.length > 0)
			return values[0].value;
		else
			return default_value;
	}

	private PALAVRACHAVE palavraChave(String str) {
		PALAVRACHAVE pl = new PALAVRACHAVE();
		pl.setPALAVRACHAVE(str);
		return pl;
	}

	private String getProperty(String param, boolean warn) {
		String value = ConfigurationManager.getProperty(param);
		if (value == null && warn)
			log.warn("DeGois add-on: Property " + param
					+ " undefined, please check your configuration file");
		return value;
	}

	private String cutString(String str, int c) {
		if (str == null)
			return str;
		if (str.length() > c) {
			return str.substring(0, c);
		} else
			return str;
	}

	private boolean process (ItemWrapper wrapper) {
		DeGoisWebpage.log.trace("DeGois add-on: Active");

		String deGoisUrl = ConfigurationManager
				.getProperty(DeGoisWebpage.CONFIG_URL);
		if (deGoisUrl == null) {
			deGoisUrl = "http://www.degois.pt/WSRCAAP/services/XMLRCAAP";
			DeGoisWebpage.log
					.info("DeGois add-on: Service URL indefined using default hard-coded = "
							+ deGoisUrl);
		}

		String user = request.getParameter("degois_user");
		String password = request.getParameter("degois_pass");
		String area = request.getParameter("degois_area");

		if (user == null) {
			this.dispatch("degois"); // Show initial page
			return false;
		}

		DeGoisWebpage.log.trace("DeGois add-on: Login Username = " + user);
		DeGoisWebpage.log.trace("DeGois add-on: Area = " + area);

		INVESTIGADOR login = new INVESTIGADOR();
		login.setUTILIZADOR(user);
		login.setPALAVRACHAVE(password);

		DeGoisWebpage.log
				.trace("DeGois add-on: Login object created and populated");

		try {
			org.um.dsi.gavea.wsrcaap.response.researcher.INVESTIGADOR degoisResponse = Util
					.send(deGoisUrl, login);
			if (degoisResponse.hasError()) {

				DeGoisWebpage.log.info("DeGois add-on: Login Erro = "
						+ degoisResponse.getERRO());
				this.ShowSubPage("UserError");
				return false;

			} else {

				PRODUCAO production = new PRODUCAO();
				production
						.setIDINVESTIGADOR(degoisResponse.getIDINVESTIGADOR());

				// Get Repository ID
				String id = ConfigurationManager
						.getProperty(DeGoisWebpage.CONFIG_REP_ID);
				if (id == null) {
					id = "000000000000000";
					DeGoisWebpage.log
							.error("DeGois add-on: Repository ID not found, be sure you have correctly defined "
									+ DeGoisWebpage.CONFIG_REP_ID);
				} else
					DeGoisWebpage.log.trace("DeGois add-on: Repository ID = "
							+ id);
				production.setIDREPOSITORIO(id);

				// Building Production from DSpace Item
				Item item = wrapper.getItem();
				String dspaceDocType = this.getValue(
						item.getMetadata("dc", "type", Item.ANY, Item.ANY),
						"other");
				// Get the degois mapping document type
				String degoisDocType = this
						.getMap()
						.getProperty(dspaceDocType, "outra-producao-cientifica")
						.trim();

				// Setting ID
				production.setIDHANDLE(Integer.toString(item.getID()));

				ElAreasConhec conh = new ElAreasConhec();
				AREADOCONHECIMENTO areac = new AREADOCONHECIMENTO();
				areac.setNOMEGRANDEAREADOCONHECIMENTO(area);
				conh.getAREADOCONHECIMENTO().add(areac);

				// Get correctly formatted date
				SimpleDateFormat outputDate = new SimpleDateFormat("dd-MM-yyyy");
				String dspaceDate = this.getValue(
						item.getMetadata("dc", "date", "issued", Item.ANY),
						null);
				Date dspaceParsedDate = new Date();
				if (dspaceDate != null && !dspaceDate.equals("")) {
					DCDate dcDate = new DCDate(dspaceDate);
					dspaceParsedDate = dcDate.toDate();
					if (dspaceParsedDate == null) {
						dspaceParsedDate = new Date();
					}
				}

				String degoisDate = outputDate.format(dspaceParsedDate);
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(dspaceParsedDate.getTime());

				// Getting year
				String year = Integer.toString(cal.get(Calendar.YEAR));

				// Getting General Data
				String dspaceTitle = this.getValue(item.getMetadata("dc",
						"title", Item.ANY, Item.ANY), this.getProperty(
						DeGoisWebpage.CONFIG_DSPACE_TITLE, true));
				if (dspaceTitle != null)
					dspaceTitle = this.cutString(dspaceTitle, 400);
				String degoisTitle = this.getValue(item.getMetadata("degois",
						"publication", "title", Item.ANY), this.getProperty(
						DeGoisWebpage.CONFIG_DEGOIS_TITLE, true));
				if (degoisTitle != null)
					degoisTitle = this.cutString(degoisTitle, 255);
				String dspacePublisher = this.getValue(
						item.getDC("publisher", Item.ANY, Item.ANY),
						this.getProperty(DeGoisWebpage.CONFIG_PUBLISHER, true));
				if (dspacePublisher != null)
					dspacePublisher = this.cutString(dspacePublisher, 30);
				String degoisNumber = this.getValue(item.getMetadata("degois",
						"publication", "issue", Item.ANY), this.getProperty(
						DeGoisWebpage.CONFIG_NUMBER, true));
				if (degoisNumber != null)
					degoisNumber = this.cutString(degoisNumber, 10);
				String degoisInitialPage = this.getValue(item.getMetadata(
						"degois", "publication", "firstPage", Item.ANY), this
						.getProperty(DeGoisWebpage.CONFIG_FIRSTPAGE, true));
				if (degoisInitialPage != null)
					degoisInitialPage = this.cutString(degoisInitialPage, 20);
				String degoisFinalPage = this.getValue(item.getMetadata(
						"degois", "publication", "lastPage", Item.ANY), this
						.getProperty(DeGoisWebpage.CONFIG_LASTPAGE, true));
				if (degoisFinalPage != null)
					degoisFinalPage = this.cutString(degoisFinalPage, 20);
				String degoisLocation = this.getValue(item.getMetadata(
						"degois", "publication", "location", Item.ANY), this
						.getProperty(DeGoisWebpage.CONFIG_LOCATION, true));
				if (degoisLocation != null)
					degoisLocation = this.cutString(degoisLocation, 30);
				String degoisVolume = this.getValue(item.getMetadata("degois",
						"publication", "volume", Item.ANY), null);
				if (degoisVolume != null)
					degoisVolume = this.cutString(degoisVolume, 30);
				String dspaceLanguage = this.getValue(
						item.getMetadata("dc", "language", Item.ANY, Item.ANY),
						this.getProperty(DeGoisWebpage.CONFIG_LANGUAGE, true));
				if (dspaceLanguage != null)
					dspaceLanguage = this.cutString(dspaceLanguage, 15);
				String dspaceISSN = this.getValue(
						item.getMetadata("dc", "identifier", "issn", Item.ANY),
						null);
				if (dspaceISSN != null)
					dspaceISSN = this.cutString(dspaceISSN, 17);
				String dspaceISBN = this.getValue(
						item.getMetadata("dc", "identifier", "isbn", Item.ANY),
						null);
				if (dspaceISBN != null)
					dspaceISBN = this.cutString(dspaceISBN, 17);
				String dspaceSerialNumber = this.getValue(item.getMetadata(
						"dc", "relation", "ispartofseries", Item.ANY), null);
				if (dspaceSerialNumber != null)
					dspaceSerialNumber = this.cutString(dspaceSerialNumber, 10);

				if (degoisDocType.equals("outra-producao-cientifica")) {
					OUTRAPRODUCAOCIENTIFICAType info = new OUTRAPRODUCAOCIENTIFICAType();

					info.setAREASDECONHECIMENTO(conh);

					DADOSBASICOS basicos = new DADOSBASICOS();

					basicos.setANO(year);
					basicos.setTITULO(dspaceTitle);
					basicos.setIDIOMA(dspaceLanguage);

					info.setDADOSBASICOS(basicos);

					DESCRICAO desc = new DESCRICAO();
					desc.setNOMEDAEDITORA(dspacePublisher);
					desc.setLOCALDEEDICAO(degoisLocation);
					if (dspaceISSN != null)
						desc.setISSNISBN(dspaceISSN);
					else if (dspaceISBN != null)
						desc.setISSNISBN(dspaceISBN);
					else {
						DeGoisWebpage.log
								.trace("DeGois add-on: ISSN/ISBN undefined");
					}

					info.setDESCRICAO(desc);

					Metadatum[] values = item.getMetadata("dc", "contributor",
							"author", Item.ANY);
					int i = 1;
					for (Metadatum value : values) {
						org.um.dsi.gavea.wsrcaap.request.rcaap.OUTRAPRODUCAOCIENTIFICAType.AUTORES autor = new org.um.dsi.gavea.wsrcaap.request.rcaap.OUTRAPRODUCAOCIENTIFICAType.AUTORES();

						autor.setNOME(this.cutString(value.value, 60));
						autor.setORDEMDEAUTORIA(new BigInteger(i + ""));
						i++;
						info.getAUTORES().add(autor);
					}
					if (values.length == 0) {
						DeGoisWebpage.log
								.warn("DeGois add-on: Document without author");
						org.um.dsi.gavea.wsrcaap.request.rcaap.OUTRAPRODUCAOCIENTIFICAType.AUTORES autor = new org.um.dsi.gavea.wsrcaap.request.rcaap.OUTRAPRODUCAOCIENTIFICAType.AUTORES();

						autor.setNOME(this.getProperty(
								DeGoisWebpage.CONFIG_AUTHOR, true));
						autor.setORDEMDEAUTORIA(new BigInteger("1"));
						info.getAUTORES().add(autor);
					}

					ElPalChave subjects = new ElPalChave();

					Metadatum[] subjs = item.getMetadata("dc", "subject",
							Item.ANY, Item.ANY);
					for (Metadatum dc : subjs)
						subjects.getPALAVRACHAVE()
								.add(this.palavraChave(this.cutString(dc.value,
										50)));
					while (subjects.getPALAVRACHAVE().size() > KEYWORDS_LIST_SIZE)
						subjects.getPALAVRACHAVE().remove(subjects.getPALAVRACHAVE().size()-1);

					info.setPALAVRASCHAVE(subjects);
					production.setOUTRAPRODUCAOCIENTIFICA(info);
				} else if (degoisDocType.equals("artigo-cientifico")) {
					ARTIGOCIENTIFICOType artigo = new ARTIGOCIENTIFICOType();
					artigo.setAREASDECONHECIMENTO(conh);

					DADOSDOARTIGO dados = new DADOSDOARTIGO();

					dados.setANODOARTIGO(year);
					dados.setTITULODOARTIGO(dspaceTitle);
					dados.setSITUACAO(this.getProperty(CONFIG_SITUATION, true));
					dados.setIDIOMA(dspaceLanguage);

					artigo.setDADOSDOARTIGO(dados);

					DADOSDAPUBLICACAO pub = new DADOSDAPUBLICACAO();
					pub.setPAGINAINICIAL(degoisInitialPage);
					pub.setPAGINAFINAL(degoisFinalPage);
					pub.setNUMERO(degoisNumber);
					pub.setTITULOPERIODICOOUREVISTA(degoisTitle);
					if (degoisVolume != null) {
						pub.setVOLUME(degoisVolume);
					}
					pub.setLOCALDEPUBLICACAO(degoisLocation);

					if (dspaceSerialNumber != null)
						pub.setSERIE(dspaceSerialNumber);
					if (dspaceISSN != null)
						pub.setISSN(dspaceISSN);

					Metadatum[] values = item.getMetadata("dc", "contributor",
							"author", Item.ANY);
					int i = 1;
					for (Metadatum value : values) {
						AUTORES autor = new AUTORES();

						autor.setNOME(this.cutString(value.value, 60));
						autor.setORDEMDEAUTORIA(new BigInteger(i + ""));
						i++;
						artigo.getAUTORES().add(autor);
					}

					if (values.length == 0) {
						log.warn("DeGois add-on: Document without author");
						AUTORES autor = new AUTORES();

						autor.setNOME(this.getProperty(CONFIG_AUTHOR, true));
						autor.setORDEMDEAUTORIA(new BigInteger("1"));

						artigo.getAUTORES().add(autor);
					}

					artigo.setDADOSDAPUBLICACAO(pub);
					ElPalChave subjects = new ElPalChave();

					Metadatum[] subjs = item.getMetadata("dc", "subject",
							Item.ANY, Item.ANY);
					for (Metadatum dc : subjs)
						subjects.getPALAVRACHAVE()
								.add(this.palavraChave(this.cutString(dc.value,
										50)));

					while (subjects.getPALAVRACHAVE().size() > KEYWORDS_LIST_SIZE)
						subjects.getPALAVRACHAVE().remove(subjects.getPALAVRACHAVE().size()-1);
					artigo.setPALAVRASCHAVE(subjects);

					production.setARTIGOCIENTIFICO(artigo);
				} else if ("livro-publicado".equals(degoisDocType)) {
					LIVROPUBLICADOType livro = new LIVROPUBLICADOType();

					// <LIVRO-PUBLICADO>
					// <DADOS-BASICOS-DO-LIVRO
					// TITULO-DO-LIVRO="TITULO-DO-LIVRO1" ANO="1000"/>
					// <DESCRICAO-DO-LIVRO NUMERO-DA-EDICAO="NUM"
					// LOCAL-DE-EDICAO="LOCAL-DE-EDICAO1"
					// NOME-DA-EDITORA="NOME-DA-EDITORA1"/>
					// <AUTORES NOME="NOME4" ORDEM-DE-AUTORIA="1073741824"/>
					// <AUTORES NOME="NOME5" ORDEM-DE-AUTORIA="1073741824"/>
					// </LIVRO-PUBLICADO>

					DADOSBASICOSDOLIVRO dados = new DADOSBASICOSDOLIVRO();
					dados.setTITULODOLIVRO(dspaceTitle);
					dados.setANO(year);
					dados.setIDIOMA(dspaceLanguage);

					livro.setDADOSBASICOSDOLIVRO(dados);

					DESCRICAODOLIVRO desc = new DESCRICAODOLIVRO();
					desc.setNUMERODAEDICAO(degoisNumber);
					desc.setLOCALDEEDICAO(degoisLocation);
					desc.setNOMEDAEDITORA(dspacePublisher);
					if (dspaceSerialNumber != null)
						desc.setNUMERODASERIE(dspaceSerialNumber);
					if (dspaceISBN != null)
						desc.setISBN(dspaceISBN);

					livro.setDESCRICAODOLIVRO(desc);

					Metadatum[] values = item.getMetadata("dc", "contributor",
							"author", Item.ANY);
					int i = 1;
					for (Metadatum value : values) {
						org.um.dsi.gavea.wsrcaap.request.rcaap.LIVROPUBLICADOType.AUTORES autor = new org.um.dsi.gavea.wsrcaap.request.rcaap.LIVROPUBLICADOType.AUTORES();

						autor.setNOME(this.cutString(value.value, 60));
						autor.setORDEMDEAUTORIA(new BigInteger(i + ""));
						i++;
						livro.getAUTORES().add(autor);
					}

					if (values.length == 0) {
						log.warn("DeGois add-on: Document without author");
						org.um.dsi.gavea.wsrcaap.request.rcaap.LIVROPUBLICADOType.AUTORES autor = new org.um.dsi.gavea.wsrcaap.request.rcaap.LIVROPUBLICADOType.AUTORES();

						autor.setNOME(this.getProperty(CONFIG_AUTHOR, true));
						autor.setORDEMDEAUTORIA(new BigInteger("1"));

						livro.getAUTORES().add(autor);
					}
					ElPalChave subjects = new ElPalChave();

					Metadatum[] subjs = item.getMetadata("dc", "subject",
							Item.ANY, Item.ANY);
					for (Metadatum dc : subjs)
						subjects.getPALAVRACHAVE()
								.add(this.palavraChave(this.cutString(dc.value,
										50)));

					while (subjects.getPALAVRACHAVE().size() > KEYWORDS_LIST_SIZE)
						subjects.getPALAVRACHAVE().remove(subjects.getPALAVRACHAVE().size()-1);
					livro.setPALAVRASCHAVE(subjects);
					production.setLIVROPUBLICADO(livro);
					/* BEGIN - ADDED by hsilva */
				} else if ("capitulo-de-livro-publicado".equals(degoisDocType)) {
					// LIVROPUBLICADOType livro = new LIVROPUBLICADOType();
					// DADOSBASICOSDOLIVRO dados = new DADOSBASICOSDOLIVRO();
					// dados.setTITULODOLIVRO(dspaceTitle);
					// dados.setANO(year);
					// dados.setIDIOMA(dspaceLanguage);
					// livro.setDADOSBASICOSDOLIVRO(dados);

					CAPITULODELIVROPUBLICADOType capitulo = new CAPITULODELIVROPUBLICADOType();
					DADOSBASICOSDOCAPITULO dados = new DADOSBASICOSDOCAPITULO();
					dados.setTITULODOCAPITULODOLIVRO(dspaceTitle);
					dados.setANO(year);
					dados.setIDIOMA(dspaceLanguage);
					capitulo.setDADOSBASICOSDOCAPITULO(dados);

					// DESCRICAODOLIVRO desc = new DESCRICAODOLIVRO();
					// desc.setNUMERODAEDICAO(degoisNumber);
					// desc.setLOCALDEEDICAO(degoisLocation);
					// desc.setNOMEDAEDITORA(dspacePublisher);
					// if (dspaceSerialNumber != null)
					// desc.setNUMERODASERIE(dspaceSerialNumber);
					// if (dspaceISBN != null) desc.setISBN(dspaceISBN);
					// livro.setDESCRICAODOLIVRO(desc);

					DESCRICAODOCAPITULO desc = new DESCRICAODOCAPITULO();
					desc.setTITULODOLIVRO(dspaceTitle);
					desc.setPAGINAINICIAL(degoisInitialPage);
					desc.setPAGINAFINAL(degoisFinalPage);
					desc.setLOCALDEEDICAO(degoisLocation);
					desc.setNOMEDAEDITORA(dspacePublisher);
					if (degoisVolume != null) {
						desc.setNUMERODOVOLUME(degoisVolume);
					}
					capitulo.setDESCRICAODOCAPITULO(desc);

					Metadatum[] values = item.getMetadata("dc", "contributor",
							"author", Item.ANY);
					int i = 1;
					for (Metadatum value : values) {
						// org.um.dsi.gavea.wsrcaap.request.rcaap.LIVROPUBLICADOType.AUTORES
						// autor = new
						// org.um.dsi.gavea.wsrcaap.request.rcaap.LIVROPUBLICADOType.AUTORES();
						org.um.dsi.gavea.wsrcaap.request.rcaap.CAPITULODELIVROPUBLICADOType.AUTORES autor = new org.um.dsi.gavea.wsrcaap.request.rcaap.CAPITULODELIVROPUBLICADOType.AUTORES();

						autor.setNOME(this.cutString(value.value, 60));
						autor.setORDEMDEAUTORIA(new BigInteger(i + ""));
						i++;
						capitulo.getAUTORES().add(autor);
						// livro.getAUTORES().add(autor);
					}

					if (values.length == 0) {
						log.warn("DeGois add-on: Document without author");
						// org.um.dsi.gavea.wsrcaap.request.rcaap.LIVROPUBLICADOType.AUTORES
						// autor = new
						// org.um.dsi.gavea.wsrcaap.request.rcaap.LIVROPUBLICADOType.AUTORES();
						org.um.dsi.gavea.wsrcaap.request.rcaap.CAPITULODELIVROPUBLICADOType.AUTORES autor = new org.um.dsi.gavea.wsrcaap.request.rcaap.CAPITULODELIVROPUBLICADOType.AUTORES();

						autor.setNOME(this.getProperty(CONFIG_AUTHOR, true));
						autor.setORDEMDEAUTORIA(new BigInteger("1"));

						// livro.getAUTORES().add(autor);
						capitulo.getAUTORES().add(autor);
					}
					ElPalChave subjects = new ElPalChave();

					Metadatum[] subjs = item.getMetadata("dc", "subject",
							Item.ANY, Item.ANY);
					for (Metadatum dc : subjs)
						subjects.getPALAVRACHAVE()
								.add(this.palavraChave(this.cutString(dc.value,
										50)));

					while (subjects.getPALAVRACHAVE().size() > KEYWORDS_LIST_SIZE)
						subjects.getPALAVRACHAVE().remove(subjects.getPALAVRACHAVE().size()-1);
					// livro.setPALAVRASCHAVE(subjects);
					// production.setLIVROPUBLICADO(livro);
					capitulo.setPALAVRASCHAVE(subjects);
					production.setCAPITULODELIVROPUBLICADO(capitulo);
					/* END - ADDED by hsilva */
				} else if (degoisDocType.equals("texto-em-jornal-ou-revista")) {
					TEXTOEMJORNALOUREVISTAType texto = new TEXTOEMJORNALOUREVISTAType();
					// <TEXTO-EM-JORNAL-OU-REVISTA>
					// <DADOS-BASICOS TIPO="Jornal de notícias" TITULO="TITULO0"
					// ANO="1000"/>
					// <DESCRICAO
					// TITULO-JORNAL-OU-REVISTA="TITULO-JORNAL-OU-REVISTA0"
					// DATA-PUBLICACAO="00-00-0000"
					// PAGINA-INICIAL="PAGINA-INICIAL3"
					// PAGINA-FINAL="PAGINA-FINAL3"/>
					// <AUTORES NOME="NOME6" ORDEM-DE-AUTORIA="1073741824"/>
					// <AUTORES NOME="NOME7" ORDEM-DE-AUTORIA="1073741824"/>
					// </TEXTO-EM-JORNAL-OU-REVISTA>
					org.um.dsi.gavea.wsrcaap.request.rcaap.TEXTOEMJORNALOUREVISTAType.DADOSBASICOS dados = new org.um.dsi.gavea.wsrcaap.request.rcaap.TEXTOEMJORNALOUREVISTAType.DADOSBASICOS();
					dados.setTIPO(this.getProperty(CONFIG_JORNALTYPE, true));
					dados.setTITULO(dspaceTitle);
					dados.setANO(year);
					dados.setIDIOMA(dspaceLanguage);

					// PG 13.10.2014: Fix TTF#1087582 — RE: Deposito de Documento aprovado!
                    texto.setDADOSBASICOS (dados);

					org.um.dsi.gavea.wsrcaap.request.rcaap.TEXTOEMJORNALOUREVISTAType.DESCRICAO desc = new org.um.dsi.gavea.wsrcaap.request.rcaap.TEXTOEMJORNALOUREVISTAType.DESCRICAO();
					desc.setTITULOJORNALOUREVISTA(degoisTitle);
					desc.setDATAPUBLICACAO(degoisDate);
					desc.setPAGINAINICIAL(degoisInitialPage);
					desc.setPAGINAFINAL(degoisFinalPage);
					if (dspaceISSN != null)
						desc.setISSN(dspaceISSN);
					desc.setLOCALDEPUBLICACAO(degoisLocation);
					if (degoisVolume != null) {
						desc.setVOLUME(degoisVolume);
					}

					texto.setDESCRICAO(desc);

					Metadatum[] values = item.getMetadata("dc", "contributor",
							"author", Item.ANY);
					int i = 1;
					for (Metadatum value : values) {
						org.um.dsi.gavea.wsrcaap.request.rcaap.TEXTOEMJORNALOUREVISTAType.AUTORES autor = new org.um.dsi.gavea.wsrcaap.request.rcaap.TEXTOEMJORNALOUREVISTAType.AUTORES();

						autor.setNOME(this.cutString(value.value, 60));
						autor.setORDEMDEAUTORIA(new BigInteger(i + ""));
						i++;
						texto.getAUTORES().add(autor);
					}

					if (values.length == 0) {
						log.warn("DeGois add-on: Document without author");
						org.um.dsi.gavea.wsrcaap.request.rcaap.TEXTOEMJORNALOUREVISTAType.AUTORES autor = new org.um.dsi.gavea.wsrcaap.request.rcaap.TEXTOEMJORNALOUREVISTAType.AUTORES();

						autor.setNOME(this.getProperty(CONFIG_AUTHOR, true));
						autor.setORDEMDEAUTORIA(new BigInteger("1"));

						texto.getAUTORES().add(autor);
					}

					ElPalChave subjects = new ElPalChave();

					Metadatum[] subjs = item.getMetadata("dc", "subject",
							Item.ANY, Item.ANY);
					for (Metadatum dc : subjs)
						subjects.getPALAVRACHAVE()
								.add(this.palavraChave(this.cutString(dc.value,
										50)));

					while (subjects.getPALAVRACHAVE().size() > KEYWORDS_LIST_SIZE)
						subjects.getPALAVRACHAVE().remove(subjects.getPALAVRACHAVE().size()-1);
					texto.setPALAVRASCHAVE(subjects);
					production.setTEXTOEMJORNALOUREVISTA(texto);
				} else if (degoisDocType.equals("relatorio-de-investigacao")) {
					RELATORIODEINVESTIGACAOType relatorio = new RELATORIODEINVESTIGACAOType();

					// <RELATORIO-DE-INVESTIGACAO>
					// <DADOS-BASICOS TITULO="TITULO2" ANO="1000"/>
					// <DESCRICAO/>
					// <AUTORES NOME="NOME10" ORDEM-DE-AUTORIA="1073741824"/>
					// <AUTORES NOME="NOME11" ORDEM-DE-AUTORIA="1073741824"/>
					// </RELATORIO-DE-INVESTIGACAO>
					org.um.dsi.gavea.wsrcaap.request.rcaap.RELATORIODEINVESTIGACAOType.DADOSBASICOS dados = new org.um.dsi.gavea.wsrcaap.request.rcaap.RELATORIODEINVESTIGACAOType.DADOSBASICOS();
					dados.setANO(year);
					dados.setTITULO(dspaceTitle);
					dados.setIDIOMA(dspaceLanguage);

					relatorio.setDADOSBASICOS(dados);

					org.um.dsi.gavea.wsrcaap.request.rcaap.RELATORIODEINVESTIGACAOType.DESCRICAO desc = new org.um.dsi.gavea.wsrcaap.request.rcaap.RELATORIODEINVESTIGACAOType.DESCRICAO();
					relatorio.setDESCRICAO(desc);

					Metadatum[] values = item.getMetadata("dc", "contributor",
							"author", Item.ANY);
					int i = 1;
					for (Metadatum value : values) {
						org.um.dsi.gavea.wsrcaap.request.rcaap.RELATORIODEINVESTIGACAOType.AUTORES autor = new org.um.dsi.gavea.wsrcaap.request.rcaap.RELATORIODEINVESTIGACAOType.AUTORES();

						autor.setNOME(this.cutString(value.value, 60));
						autor.setORDEMDEAUTORIA(new BigInteger(i + ""));
						i++;
						relatorio.getAUTORES().add(autor);
					}

					if (values.length == 0) {
						log.warn("DeGois add-on: Document without author");
						org.um.dsi.gavea.wsrcaap.request.rcaap.RELATORIODEINVESTIGACAOType.AUTORES autor = new org.um.dsi.gavea.wsrcaap.request.rcaap.RELATORIODEINVESTIGACAOType.AUTORES();

						autor.setNOME(this.getProperty(CONFIG_AUTHOR, true));
						autor.setORDEMDEAUTORIA(new BigInteger("1"));

						relatorio.getAUTORES().add(autor);
					}

					ElPalChave subjects = new ElPalChave();

					Metadatum[] subjs = item.getMetadata("dc", "subject",
							Item.ANY, Item.ANY);
					for (Metadatum dc : subjs)
						subjects.getPALAVRACHAVE()
								.add(this.palavraChave(this.cutString(dc.value,
										50)));

					while (subjects.getPALAVRACHAVE().size() > KEYWORDS_LIST_SIZE)
						subjects.getPALAVRACHAVE().remove(subjects.getPALAVRACHAVE().size()-1);
					relatorio.setPALAVRASCHAVE(subjects);
					production.setRELATORIODEINVESTIGACAO(relatorio);
				} else if (degoisDocType
						.equals("apresentacao-oral-de-trabalho")) {
					APRESENTACAOORALDETRABALHOType apr = new APRESENTACAOORALDETRABALHOType();

					// <TRABALHO-EM-EVENTO-PUBLICADO>
					// <DADOS-BASICOS TIPO="Completo" TITULO="TITULO4"
					// ANO="1000"/>
					// <DESCRICAO NOME-EVENTO="NOME-EVENTO0"
					// LOCAL-DO-EVENTO="LOCAL-DO-EVENTO0"
					// ANO-REALIZACAO-EVENTO="1000"
					// TITULO-PROCEEDINGS="TITULO-PROCEEDINGS0"
					// NOME-DA-EDITORA="NOME-DA-EDITORA3"/>
					// <AUTORES NOME="NOME14" ORDEM-DE-AUTORIA="1073741824"/>
					// <AUTORES NOME="NOME15" ORDEM-DE-AUTORIA="1073741824"/>
					// </TRABALHO-EM-EVENTO-PUBLICADO>
					org.um.dsi.gavea.wsrcaap.request.rcaap.APRESENTACAOORALDETRABALHOType.DADOSBASICOS dados = new org.um.dsi.gavea.wsrcaap.request.rcaap.APRESENTACAOORALDETRABALHOType.DADOSBASICOS();
					dados.setANO(year);
					dados.setTIPO(this.getProperty(CONFIG_PRESENTATIONTYPE,
							true));
					dados.setTITULO(dspaceTitle);
					dados.setIDIOMA(dspaceLanguage);

					apr.setDADOSBASICOS(dados);

					org.um.dsi.gavea.wsrcaap.request.rcaap.APRESENTACAOORALDETRABALHOType.DESCRICAO desc = new org.um.dsi.gavea.wsrcaap.request.rcaap.APRESENTACAOORALDETRABALHOType.DESCRICAO();
					if (degoisVolume != null) {
						desc.setLOCAL(degoisVolume); // ???
					}
					apr.setDESCRICAO(desc);

					Metadatum[] values = item.getMetadata("dc", "contributor",
							"author", Item.ANY);
					int i = 1;
					for (Metadatum value : values) {
						org.um.dsi.gavea.wsrcaap.request.rcaap.APRESENTACAOORALDETRABALHOType.AUTORES autor = new org.um.dsi.gavea.wsrcaap.request.rcaap.APRESENTACAOORALDETRABALHOType.AUTORES();

						autor.setNOME(this.cutString(value.value, 60));
						autor.setORDEMDEAUTORIA(new BigInteger(i + ""));
						i++;
						apr.getAUTORES().add(autor);
					}

					if (values.length == 0) {
						log.warn("DeGois add-on: Document without author");
						org.um.dsi.gavea.wsrcaap.request.rcaap.APRESENTACAOORALDETRABALHOType.AUTORES autor = new org.um.dsi.gavea.wsrcaap.request.rcaap.APRESENTACAOORALDETRABALHOType.AUTORES();

						autor.setNOME(this.getProperty(CONFIG_AUTHOR, true));
						autor.setORDEMDEAUTORIA(new BigInteger("1"));

						apr.getAUTORES().add(autor);
					}

					ElPalChave subjects = new ElPalChave();

					Metadatum[] subjs = item.getMetadata("dc", "subject",
							Item.ANY, Item.ANY);
					for (Metadatum dc : subjs)
						subjects.getPALAVRACHAVE()
								.add(this.palavraChave(this.cutString(dc.value,
										50)));

					while (subjects.getPALAVRACHAVE().size() > KEYWORDS_LIST_SIZE)
						subjects.getPALAVRACHAVE().remove(subjects.getPALAVRACHAVE().size()-1);
					apr.setPALAVRASCHAVE(subjects);
					production.setAPRESENTACAOORALDETRABALHO(apr);
				}

				org.um.dsi.gavea.wsrcaap.response.rcaap.PRODUCAO result = Util
						.send(deGoisUrl, production);
				if (result.hasError()) {
					log.info("DeGois add-on: Sending error " + result.getERRO());

					log.warn("DeGois XML Sent: ");
					log.warn(Util.output(production));
					this.ShowSubPage("ConnectionError");
					return false;
				}
			}
		} catch (JAXBException e) {
			log.info("Error degois interaction", e);
			this.ShowSubPage("ConnectionError");
			return false;
		} catch (ServiceException e) {
			log.info("Error degois interaction", e);
			this.ShowSubPage("ConnectionError");
			return false;
		} catch (RemoteException e) {
			log.info("Error degois interaction", e);
			this.ShowSubPage("ConnectionError");
			return false;
		}
		this.ShowSubPage("Success");
		return false;
	}

	private Properties getMap() {
		Properties properties = new Properties();
		try {
			String dir = ConfigurationManager.getProperty("dspace.dir");
			if (dir.endsWith("/"))
				dir += "config/" + KNOWN_DCTYPES_FILE;
			else
				dir += "/config/" + KNOWN_DCTYPES_FILE;
			InputStream is;
			if (dir != null) {
				is = new FileInputStream(dir);
				properties.load(is);
				is.close();
			}
		} catch (Exception e) {
			log.error("DeGois Addon not Installed!");
		}
		return properties;
	}

	private void dispatch(String page) {
		try {
			request.getRequestDispatcher("/sharing/" + page + ".jsp").forward(
					request, response);
		} catch (ServletException ex) {
			log.debug(ex.getCause(), ex);
		} catch (IOException ex) {
			log.debug(ex.getCause(), ex);
		}
	}

	public void ShowSubPage (String page) {
		this.dispatch("degois" + page);
	}
}
