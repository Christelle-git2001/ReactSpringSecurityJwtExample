import { fireEvent, render, screen } from "@testing-library/react";
import ListItem from "../../component/Design/Lists/ListItem.jsx";

describe("ListItem", () => {
    it("affiche le titre, la description et les informations", () => {
        render(
            <ListItem
                title="Nouveau CV"
                subtitle="Mercedes Belle a téléversé un CV"
                meta="12 octobre 2026"
                variant="row"
            />
        );

        expect(screen.getByText("Nouveau CV")).toBeInTheDocument();
        expect(screen.getByText("Mercedes Belle a téléversé un CV")).toBeInTheDocument();
        expect(screen.getByText("12 octobre 2026")).toBeInTheDocument();
    });

    it("appelle onClick quand on clique sur un élément", () => {
        const onClick = jest.fn();
        render(
            <ListItem title="Nouveau CV" variant="row" onClick={onClick} />
        );

        fireEvent.click(screen.getByRole("button", { name: "Nouveau CV" }));

        expect(onClick).toHaveBeenCalledTimes(1);
    });

    it("affiche les informations en variante card", () => {
        render(
            <ListItem
                title="Offre de stage"
                subtitle="Développeur Java"
                variant="card"
            />
        );

        expect(screen.getByText("Offre de stage")).toBeInTheDocument();
        expect(screen.getByText("Développeur Java")).toBeInTheDocument();
    });
});