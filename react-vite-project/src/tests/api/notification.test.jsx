import { beforeEach, describe, vi } from "vitest";

vi.mock("./http.jsx", () => ({
    fetchJson: vi.fn(),
}));

beforeEach(() => {
    vi.resetAllMocks();
});

describe("API Notification", () => {

});