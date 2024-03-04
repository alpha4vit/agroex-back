package com.vention.agroex.model;

import java.time.ZonedDateTime;

public record ReportRequest(ZonedDateTime actualStartDate,
                            ZonedDateTime expirationDate,
                            String lotType,
                            Long countryId) {}
