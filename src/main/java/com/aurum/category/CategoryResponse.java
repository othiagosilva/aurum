package com.aurum.category;

import java.util.UUID;

public record CategoryResponse(UUID id, String name, CategoryType type) {

}
