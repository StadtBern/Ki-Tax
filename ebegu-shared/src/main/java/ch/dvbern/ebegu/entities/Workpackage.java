package ch.dvbern.ebegu.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Entity that represents part a single Work-Package that is part of a bigger Workjob which is partitioned into multiple workpackages
 */
@Entity
@Table(name = "workpackage", uniqueConstraints = {@UniqueConstraint(columnNames = {"workjob_id", "workPackageSeqNumber"} , name = "UK_workpkg_workjob_seq_num")})
public class Workpackage extends AbstractEntity {

	public static final String QUERY_FIND_WORKPACKAGE = "QUERY_FIND_WORKPACKAGE";
	public static final String QUERY_FIND_UNFINISHED_PACKAGE = "QUERY_FIND_UNFINISHED_PACKAGE";

	@ManyToOne
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_workpkg_workjob_id"), nullable = false, updatable = false)
	private Workjob workjob;


	@NotNull
	@Column(nullable = false)
	@Min(0)
	private Integer workPackageSeqNumber;

	@Lob
	private String workRowResult;


	public Workjob getWorkjob() {
		return workjob;
	}

	public void setWorkjob(final Workjob workjob) {
		this.workjob = workjob;
	}

	public int getWorkPackageSeqNumber() {
		return workPackageSeqNumber;
	}

	public void setWorkPackageSeqNumber(final int workPackageSeqNumber) {
		this.workPackageSeqNumber = workPackageSeqNumber;
	}

	public String getWorkRowResult() {
		return workRowResult;
	}

	public void setWorkRowResult(final String workRowResult) {
		this.workRowResult = workRowResult;
	}



	@SuppressWarnings("ObjectEquality")
	@Override
	public boolean isSame(AbstractEntity o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}

		Workpackage that = (Workpackage) o;

		if (workPackageSeqNumber != that.getWorkPackageSeqNumber()) {
			return false;
		}
		if (!workjob.equals(that.getWorkjob())) {
			return false;
		}
		return workRowResult != null ? workRowResult.equals(that.getWorkRowResult()) : that.getWorkjob() == null;
	}
}
