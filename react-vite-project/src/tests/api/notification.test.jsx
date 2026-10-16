import { fetchJson } from "../../api/http.jsx";
import { getNotifications, marquerNotificationLue } from "../../api/notification.jsx";

jest.mock("../../api/http.jsx", () => ({
    fetchJson: jest.fn(),
}));

beforeEach(() => {
    jest.resetAllMocks();
});

describe("API Notification", () => {
    it("charge les notifications de l'utilisateur", async () => {
        const notifications = [{ id: 1 }];
        fetchJson.mockResolvedValue(notifications);

        expect(await getNotifications(5)).toEqual(notifications);
        expect(fetchJson).toHaveBeenCalledWith("/notification/utilisateur/5", {});
    });

    it("marque une notification comme lue", async () => {
        fetchJson.mockResolvedValue({ id: 1, estLu: true });

        expect(await marquerNotificationLue(1)).toEqual({ id: 1, estLu: true });
        expect(fetchJson).toHaveBeenCalledWith("/notification/1/lu", { method: "PUT" });
    });
});