package ch.dvbern.ebegu.batch.jobs.report;

import org.jberet.cdi.JobScoped;

import ch.dvbern.ebegu.util.UploadFileInfo;

/**
 * Objekt zum austauschen von Daten zwischen Steps, koennte auch ueber das Datenbankobjekt Workjob gemacht werden
 */
@JobScoped
public class JobDataContainer {

	private UploadFileInfo result;


	public UploadFileInfo getResult() {
		return result;
	}

	public void setResult(UploadFileInfo result) {
		this.result = result;
	}
}
