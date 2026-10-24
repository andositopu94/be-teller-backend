package com.multipolar.uiteller.backend.security.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class OtherChannel {

	private String card;
	private String id;
}
