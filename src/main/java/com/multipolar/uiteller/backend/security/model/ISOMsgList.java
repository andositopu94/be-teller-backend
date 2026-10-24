package com.multipolar.uiteller.backend.security.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jpos.iso.ISOMsg;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
public class ISOMsgList {

	private List<ISOMsg> isoMsg;
}
