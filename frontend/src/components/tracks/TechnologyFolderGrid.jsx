import TechnologyFolder from './TechnologyFolder.jsx';

function TechnologyFolderGrid({ fields, onOpen }) {
  return (
    <div className="technology-folder-grid" aria-label="Teknoloji alanı klasörleri">
      {fields.map((field) => <TechnologyFolder key={field.id} field={field} onOpen={onOpen} />)}
    </div>
  );
}

export default TechnologyFolderGrid;
