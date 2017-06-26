package ch.dvbern.ebegu.api.dtos;

import ch.dvbern.ebegu.dto.JaxAntragDTO;
import ch.dvbern.ebegu.dto.suchfilter.smarttable.PaginationDTO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

/**
 * Bei der Table mit Pagination muss die Totalanzahl Resultate vom Server mit zuruckgegeben werden im Resultat
 */
@XmlRootElement(name = "pendenz")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxAntragSearchresultDTO implements Serializable{


	private static final long serialVersionUID = 3939072050781289382L;
	private List<JaxAntragDTO> antragDTOs;
	private PaginationDTO paginationDTO;

	public List<JaxAntragDTO> getAntragDTOs() {
		return antragDTOs;
	}

	public void setAntragDTOs(List<JaxAntragDTO> antragDTOs) {
		this.antragDTOs = antragDTOs;
	}

	public PaginationDTO getPaginationDTO() {
		return paginationDTO;
	}

	public void setPaginationDTO(PaginationDTO paginationDTO) {
		this.paginationDTO = paginationDTO;
	}
}
